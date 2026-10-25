import {
  keepPreviousData,
  useMutation,
  useQuery,
  useQueryClient,
} from "@tanstack/react-query";
import { useEffect, useState } from "react";
import {
  apagarAnexo,
  enviarAnexo,
  getAnexos,
  getAnexosPorData,
} from "../services/anexoService";
import { toast } from "sonner";

import { TipoArquivo } from "@/features/anexo/types";
import type { AnexoEnvioFormData } from "@/features/anexo/types";

import { validarTipoArquivo } from "@/services/validarTipoArquivo";
import { validarTamanhoArquivo } from "@/services/validarTamanhoArquivo";
import { construirArquivoFormData } from "@/services/construirArquivoFormData";

import type { Anexo, AnexosPaginados } from "../types";

export function useAnexos(pacienteId: string) {
  const [dataSelecionada, setDataSelecionada] = useState("");
  const [page, setPage] = useState(1);
  const [open, setOpen] = useState(false);

  const [reportToView, setReportToView] = useState<Anexo | null>(null);
  const [reportToDelete, setReportToDelete] = useState<Anexo | null>(null);

  const queryClient = useQueryClient();

  const { data, isLoading, isFetching } = useQuery<AnexosPaginados>({
    queryKey: ["anexos", pacienteId, dataSelecionada, page],
    enabled: !!pacienteId,
    placeholderData: keepPreviousData,
    queryFn: async () => {
      // com data selecionada, usa a busca por data (sem paginação)
      if (dataSelecionada) {
        const anexos = await getAnexosPorData(pacienteId, dataSelecionada);
        return { anexos, pagination: null };
      }
      return getAnexos(pacienteId, page);
    },
  });

  const anexos = data?.anexos ?? [];
  const pagination = data?.pagination ?? null;

  // ao selecionar (ou limpar) a data, volta para a primeira página
  const handleSelecionarData = (novaData: string) => {
    setDataSelecionada(novaData);
    setPage(1);
  };

  // se a última página ficar vazia (ex.: após remover o único item), volta uma página
  useEffect(() => {
    if (!isFetching && !dataSelecionada && page > 1 && anexos.length === 0) {
      setPage((p) => Math.max(1, p - 1));
    }
  }, [isFetching, dataSelecionada, page, anexos.length]);

  const enviarAnexoMutation = useMutation({
    mutationFn: enviarAnexo,
    onSuccess: () => {
      toast.success("Anexo criado com sucesso!");
      setDataSelecionada("");
      setPage(1);
      queryClient.invalidateQueries({ queryKey: ["anexos", pacienteId] });
      setOpen(false);
    },
    onError: (error: unknown) => {
      const mensagem = error instanceof Error ? error.message : "Erro ao enviar";
      toast.error(mensagem);
    },
  });

  const deletarAnexoMutation = useMutation({
    mutationFn: apagarAnexo,
    onSuccess: () => {
      toast.success("Removido com sucesso!");
      queryClient.invalidateQueries({ queryKey: ["anexos", pacienteId] });
      setReportToDelete(null);
    },
    onError: () => {
      toast.error("Erro ao remover");
    },
  });

  const handleEnviarAnexo = (formData: AnexoEnvioFormData) => {
    try {
      if (!formData.data) throw new Error("Data obrigatória");
      if (!formData.titulo?.trim()) throw new Error("Título obrigatório");
      if (!formData.arquivo?.length) throw new Error("Arquivo obrigatório");

      // ✅ NORMALIZAÇÃO
      const titulo = formData.titulo.trim().replace(/\s+/g, " ");
      const descricao = formData.descricao?.trim().replace(/\s+/g, " ") || "";

      // ✅ VALIDAÇÃO TÍTULO
      const tituloRegex = /^(?=.*[\p{L}\p{M}])[\p{L}\p{M}0-9 \-:/()']*$/u;

      if (!tituloRegex.test(titulo)) {
        throw new Error("Título inválido");
      }

      // ✅ VALIDAÇÃO DESCRIÇÃO
      const descRegex = /^(?=.*[\p{L}\p{M}])[\p{L}\p{M}0-9 \-:/()'%&#]*$/u;

      if (!descRegex.test(descricao)) {
        throw new Error("Descrição inválida");
      }

      // ✅ REGRA: não pode ser só número
      const cleanTitulo = titulo.replace(/\s/g, "");
      if (!cleanTitulo || /^\d+$/.test(cleanTitulo)) {
        throw new Error("Título inválido");
      }

      const cleanDesc = descricao.replace(/\s/g, "");
      if (!cleanDesc || /^\d+$/.test(cleanDesc)) {
        throw new Error("Descrição inválida");
      }

      const request: AnexoEnvioFormData = {
        ...formData,
        titulo,
        descricao,
        pacienteId,
        tipoArquivo: TipoArquivo.anexo,
      };

      // ✅ VALIDA ARQUIVO
      validarTipoArquivo(request.arquivo);
      validarTamanhoArquivo(request.arquivo);

      const ready = construirArquivoFormData(request);

      enviarAnexoMutation.mutate(ready);
    } catch (error: unknown) {
      const mensagem = error instanceof Error ? error.message : "Erro ao enviar anexo";
      toast.error(mensagem);
    }
  };
  return {
    anexos,
    loading: isLoading,

    pagination,
    isFetching,
    setPage,

    dataSelecionada,
    setDataSelecionada: handleSelecionarData,

    open,
    setOpen,

    reportToView,
    setReportToView,

    reportToDelete,
    setReportToDelete,

    enviarAnexo: handleEnviarAnexo,
    deletarAnexo: deletarAnexoMutation.mutate,

    enviando: enviarAnexoMutation.isPending,
    deletando: deletarAnexoMutation.isPending,
  };
}