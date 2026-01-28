"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useMemo, useState, useEffect } from "react"; // Adicionado useEffect
import { apagarAnexo, enviarAnexo, getAnexos } from "../services/anexoService";
import { toast } from "sonner";
import { AxiosError } from "axios";
import {
  AnexoEnvioFormData,
  TipoArquivo,
} from "@/features/anexo/components/anexoForm";
// Removido o import de dados
import { validarTipoArquivo } from "@/services/validarTipoArquivo";
import { validarTamanhoArquivo } from "@/services/validarTamanhoArquivo";
import { construirArquivoFormData } from "@/services/construirArquivoFormData";
import { Anexo, AnexoResponse } from "../types";
import { filtroData } from "@/utils/filtroData";

export function useAnexos(pacienteId: string) {
  const [dataSelecionada, setDataSelecionada] = useState<string>("");
  const [open, setOpen] = useState(false);
  const [reportToDelete, setReportToDelete] = useState<Anexo | null>(null);
  const [reportToView, setReportToView] = useState<Anexo | null>(null);

  // --- BUSCA DO PROFISSIONAL ---
  const [profissionalId, setProfissionalId] = useState<string>("");

  useEffect(() => {
    if (typeof window !== "undefined") {
      const stored = localStorage.getItem("@apae:profissional");
      if (stored) {
        try {
          const parsed = JSON.parse(stored);
          setProfissionalId(parsed.id);
        } catch (error) {
          console.error("Erro ao ler dados do profissional", error);
        }
      }
    }
  }, []);
  // -----------------------------

  const queryClient = useQueryClient();

  const { data, isLoading } = useQuery({
    queryKey: ["anexos", pacienteId],
    enabled: !!pacienteId,
    queryFn: async () => {
      const [anexosResponse] = await Promise.all([getAnexos(pacienteId)]);

      const anexos: Anexo[] = anexosResponse.map((e: AnexoResponse, i: number) => ({
        id: i + 1,
        ...e,
      }));
      return { anexos };
    },
  });

  const anexosFiltrados = useMemo(() => {
    if (!data?.anexos) return [];
    if (!dataSelecionada) return data.anexos;

    return data.anexos.filter((anexo) =>
      filtroData(dataSelecionada, anexo.data),
    );
  }, [data, dataSelecionada]);

  const enviarAnexoMutation = useMutation({
    mutationFn: enviarAnexo,
    onSuccess: () => {
      toast.success("Anexo criado com sucesso!");
      queryClient.invalidateQueries({
        queryKey: ["anexos", pacienteId],
      });
    },
    onError: (error: unknown) => {
      const mensagem =
        error instanceof AxiosError
          ? error.response?.data || error.message
          : error instanceof Error
            ? error.message
            : String(error);

      toast.error(mensagem || "Erro ao enviar anexo");
    },
    onSettled() {
      setOpen(false);
    },
  });

  const deletarAnexoMutation = useMutation({
    mutationFn: apagarAnexo,
    onSuccess: () => {
      toast.success("Anexo removido");
      queryClient.invalidateQueries({
        queryKey: ["anexos", pacienteId],
      });
    },
    onError: () => {
      toast.error("Erro ao remover anexos");
    },
  });

  // Alterada para usar o profissionalId do estado
  function criarArquivoAnexo(data: AnexoEnvioFormData): FormData {
    if (!profissionalId) {
      throw new Error("Profissional não identificado. Por favor, faça login novamente.");
    }

    const request: AnexoEnvioFormData = {
      ...data,
      pacienteId,
      tipoArquivo: TipoArquivo.anexo,
      profissionalId: profissionalId, // Sucesso: trocado!
    };

    validarTipoArquivo(request.arquivo);
    validarTamanhoArquivo(request.arquivo);
    return construirArquivoFormData(request);
  }

  async function construirEnviarArquivoAnexo(data: AnexoEnvioFormData) {
    try {
      const anexo = criarArquivoAnexo(data);
      enviarAnexoMutation.mutate(anexo);
    } catch (error: any) {
      toast.error(error.message);
    }
  }

  const handleDelete = (objectName: string) => {
    deletarAnexoMutation.mutate(objectName);
  };

  return {
    anexos: data?.anexos ?? [],
    loading: isLoading,
    dataSelecionada,
    open,
    reportToDelete,
    reportToView,
    anexosFiltrados,
    setDataSelecionada,
    setOpen,
    setReportToDelete,
    setReportToView,
    enviarAnexo: construirEnviarArquivoAnexo,
    deletarAnexo: handleDelete,
    enviando: enviarAnexoMutation.isPending,
    deletando: deletarAnexoMutation.isPending,
  };
}