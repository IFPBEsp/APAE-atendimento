"use client";

import {
  Relatorio,
  RelatorioEnvioFormData,
  RelatorioBase,
} from "@/features/relatorio/types";

import dados from "@/../data/verificacao.json";
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
import { useMemo, useState } from "react";
import { TipoArquivo } from "@/features/arquivo/types";
import { filtroData } from "@/utils/filtroData";

export function useRelatorios(pacienteId: string) {
  const [dataSelecionada, setDataSelecionada] = useState<string>("");
  const [open, setOpen] = useState(false);
  const [reportToDelete, setReportToDelete] = useState<Relatorio | null>(null);
  const [reportToView, setReportToView] = useState<Relatorio | null>(null);
  const queryClient = useQueryClient();
  const { data, isLoading } = useQuery({
    queryKey: ["relatorios", pacienteId],
    enabled: !!pacienteId,
    queryFn: async () => {
      const [relatoriosResponse] = await Promise.all([
        getRelatorios(pacienteId),
      ]);

      const relatorios: Relatorio[] = relatoriosResponse.map(
        (e: RelatorioBase, i) => ({
          id: ++i,
          ...e,
        }),
      );
      return { relatorios };
    },
  });

  const relatoriosFiltrados = useMemo(() => {
    if (!data?.relatorios) return [];

    if (!dataSelecionada) {
      return data.relatorios;
    }

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

  function criarArquivoRelatorio(data: RelatorioEnvioFormData): FormData | undefined {
    const request: RelatorioEnvioFormData = {
      ...data,
      pacienteId,
      tipoArquivo: TipoArquivo.relatorio,
      profissionalId: dados.idProfissional,
    };

    try{
      validarTamanhoArquivo(request.arquivo);
      return construirArquivoFormData(request);
    }catch(error){
      const mensagem = error instanceof Error
            ? error.message
            : String(error);
       toast.error(mensagem || "Erro ao enviar anexo");
    }
    
  }

  async function construirEnviarArquivoRelatorio(data: RelatorioEnvioFormData) {
    const relatorio = criarArquivoRelatorio(data);
    relatorio && enviarRelatorioMutation.mutate(relatorio);
  }

  const handleDelete = (objectName: string) => {
    deletarRelatorioMutation.mutate(objectName);
  };

  return {
    // dados-estados
    loading: isLoading,
    dataSelecionada,
    open,
    reportToDelete,
    reportToView,
    relatoriosFiltrados,

    // ações-passivas
    setDataSelecionada,
    setOpen,
    setReportToDelete,
    setReportToView,

    // ações-ativas
    enviarRelatorio: construirEnviarArquivoRelatorio,
    deletarRelatorio: handleDelete,

    // intenções
    enviando: enviarRelatorioMutation.isPending,
    deletando: deletarRelatorioMutation.isPending,
  };
}
