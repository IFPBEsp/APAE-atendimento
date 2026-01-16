
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import { apagarAnexo, enviarAnexo, getAnexos } from "../services/anexoService";
import { toast } from "sonner";
import { AxiosError } from "axios";
import {
  AnexoEnvioFormData,
  TipoArquivo,
} from "@/features/anexo/components/anexoForm";
import dados from "@/../data/verificacao.json";
import { validarTipoArquivo } from "@/services/validarTipoArquivo";
import { validarTamanhoArquivo } from "@/services/validarTamanhoArquivo";
import { construirArquivoFormData } from "@/services/construirArquivoFormData";
import { handleDownload } from "@/features/arquivo/services/arquivoService";
import { Anexo, AnexoResponse } from "../types";

export function useAnexos(pacienteId: string) {
  const [dataSelecionada, setDataSelecionada] = useState<string>("");
  const [open, setOpen] = useState(false);
  const [reportToDelete, setReportToDelete] = useState<Anexo | null>(null);
  const [reportToView, setReportToView] = useState<Anexo | null>(null);

  const queryClient = useQueryClient();

  const { data, isLoading } = useQuery({
    queryKey: ["anexos", pacienteId],
    enabled: !!pacienteId,
    queryFn: async () => {
      const [anexosResponse] = await Promise.all([getAnexos(pacienteId)]);

      const anexos: Anexo[] = anexosResponse.map((e: AnexoResponse, i) => ({
        id: ++i,
        ...e,
      }));
      return { anexos };
    },
  });

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

  function criarArquivoAnexo(data: AnexoEnvioFormData): FormData {
    const request: AnexoEnvioFormData = {
      ...data,
      pacienteId,
      tipoArquivo: TipoArquivo.anexo,
      profissionalId: dados.idProfissional,
    };

    validarTipoArquivo(request.arquivo);
    validarTamanhoArquivo(request.arquivo);
    return construirArquivoFormData(request);
  }

  async function construirEnviarArquivoAnexo(data: AnexoEnvioFormData) {
    const anexo = criarArquivoAnexo(data);
    enviarAnexoMutation.mutate(anexo);
  }

  const handleDelete = (objectName: string) => {
    deletarAnexoMutation.mutate(objectName);
  };

  const handleUpdate = async (objectName: string) => {
    if (!objectName) return;
    await handleDownload(objectName);
  };

  return {
    // dados-estados
    anexos: data?.anexos ?? [],
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
    enviarAnexo: construirEnviarArquivoAnexo,
    deletarAnexo: handleDelete,
    baixarAnexo: handleUpdate,

    // intenções
    enviando: enviarAnexoMutation.isPending,
    deletando: deletarAnexoMutation.isPending,
  };
}
