"use client";

import { Relatorio, RelatorioEnvioFormData, RelatorioResponse } from "@/features/relatorio/types";

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
import { useState } from "react";
import { TipoArquivo } from "@/features/arquivo/types";


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
        (e: RelatorioResponse, i) => ({
          id: ++i,
          ...e,
        })
      );
      return { relatorios };
    },
  });

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

  function criarArquivoRelatorio(data: RelatorioEnvioFormData): FormData {
    const request: RelatorioEnvioFormData = {
      ...data,
      pacienteId,
      tipoArquivo: TipoArquivo.relatorio,
      profissionalId: dados.idProfissional,
    };

    validarTamanhoArquivo(request.arquivo);
    return construirArquivoFormData(request);
  }

  async function construirEnviarArquivoRelatorio(data: RelatorioEnvioFormData) {
    const relatorio = criarArquivoRelatorio(data);
    enviarRelatorioMutation.mutate(relatorio);
  }

  const handleDelete = (objectName: string) => {
    deletarRelatorioMutation.mutate(objectName);
  };


  return {
    // dados-estados
    relatorios: data?.relatorios ?? [],
    loading: isLoading,
    dataSelecionada,
    open,
    reportToDelete,
    reportToView,

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
    deletando: deletarRelatorioMutation.isPending
  };
}
