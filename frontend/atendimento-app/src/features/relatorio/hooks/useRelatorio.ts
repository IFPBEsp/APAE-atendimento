"use client";

import { useEffect, useState, useMemo } from "react"; // Adicionado useEffect
import {
  Relatorio,
  RelatorioEnvioFormData,
  RelatorioBase,
} from "@/features/relatorio/types";

import { toast } from "sonner";
import { validarTamanhoArquivo } from "@/services/validarTamanhoArquivo";
import { construirArquivoFormData } from "@/services/construirArquivoFormData";
import { apagarAnexo } from "@/features/anexo/services/anexoService";

import { AxiosError } from "axios";
import {
  enviarRelatorio,
  getRelatorios,
} from "@/features/relatorio/services/relatorioService";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { TipoArquivo } from "@/features/arquivo/types";
import { filtroData } from "@/utils/filtroData";

export function useRelatorios(pacienteId: string) {
  const [dataSelecionada, setDataSelecionada] = useState<string>("");
  const [open, setOpen] = useState(false);
  const [reportToDelete, setReportToDelete] = useState<Relatorio | null>(null);
  const [reportToView, setReportToView] = useState<Relatorio | null>(null);
  
  const [profissionalId, setProfissionalId] = useState<string>("");

  useEffect(() => {
    if (typeof window !== "undefined") {
      const stored = localStorage.getItem("@apae:profissional");
      if (stored) {
        const parsed = JSON.parse(stored);
        setProfissionalId(parsed.id);
      }
    }
  }, []);
  // ----------------------------------------------

  const queryClient = useQueryClient();

  const { data, isLoading } = useQuery({
    queryKey: ["relatorios", pacienteId],
    enabled: !!pacienteId,
    queryFn: async () => {
      const relatoriosResponse = await getRelatorios(profissionalId, pacienteId);

      const relatorios: Relatorio[] = relatoriosResponse.map(
        (e: RelatorioBase, i: number) => ({
          id: i + 1,
          ...e,
        }),
      );
      return { relatorios };
    },
  });

  const relatoriosFiltrados = useMemo(() => {
    if (!data?.relatorios) return [];
    if (!dataSelecionada) return data.relatorios;

    return data.relatorios.filter((relatorio) =>
      filtroData(dataSelecionada, relatorio.data),
    );
  }, [data, dataSelecionada]);

  const enviarRelatorioMutation = useMutation({
    mutationFn: enviarRelatorio,
    onSuccess: () => {
      toast.success("Relatório criado com sucesso!");
      queryClient.invalidateQueries({
        queryKey: ["relatorios", pacienteId],
      });
    },
    onError: (error: unknown) => {
      const mensagem =
        error instanceof AxiosError
          ? error.response?.data || error.message
          : error instanceof Error
            ? error.message
            : String(error);

      toast.error(mensagem || "Erro ao enviar relatório");
    },
    onSettled() {
      setOpen(false);
    },
  });

  const deletarRelatorioMutation = useMutation({
    mutationFn: apagarAnexo,
    onSuccess: () => {
      toast.success("Relatório removido");
      queryClient.invalidateQueries({
        queryKey: ["relatorios", pacienteId],
      });
    },
    onError: () => {
      toast.error("Erro ao remover relatório");
    },
  });

  /** 🔥 TROCA DO dados.idProfissional PELO profissionalId DO ESTADO */
  function criarArquivoRelatorio(data: RelatorioEnvioFormData): FormData {
    if (!profissionalId) {
      throw new Error("Profissional não identificado. Refaça o login.");
    }

    const request: RelatorioEnvioFormData = {
      ...data,
      pacienteId,
      tipoArquivo: TipoArquivo.relatorio,
      profissionalId: profissionalId, // Agora usa o ID do localStorage
    };

    validarTamanhoArquivo(request.arquivo);
    return construirArquivoFormData(request);
  }

  async function construirEnviarArquivoRelatorio(data: RelatorioEnvioFormData) {
    try {
      const relatorio = criarArquivoRelatorio(data);
      enviarRelatorioMutation.mutate(relatorio);
    } catch (error: any) {
      toast.error(error.message);
    }
  }

  const handleDelete = (objectName: string) => {
    deletarRelatorioMutation.mutate(objectName);
  };

  return {
    loading: isLoading,
    dataSelecionada,
    open,
    reportToDelete,
    reportToView,
    relatoriosFiltrados,
    setDataSelecionada,
    setOpen,
    setReportToDelete,
    setReportToView,
    enviarRelatorio: construirEnviarArquivoRelatorio,
    deletarRelatorio: handleDelete,
    enviando: enviarRelatorioMutation.isPending,
    deletando: deletarRelatorioMutation.isPending,
  };
}