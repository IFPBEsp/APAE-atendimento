import { useEffect, useState } from "react";
import {
  keepPreviousData,
  useMutation,
  useQuery,
  useQueryClient,
} from "@tanstack/react-query";
import {
  apagarAnexo,
  enviarAnexo,
  getAnexos,
  getAnexosPorData,
} from "../services/anexoService";
import { toast } from "sonner";

import { TipoArquivo } from "@/features/anexo/types";
import type { AnexoEnvioFormData } from "@/features/anexo/types";
import type { ArquivoPagination } from "@/features/arquivo/types";

import { validarTipoArquivo } from "@/services/validarTipoArquivo";
import { validarTamanhoArquivo } from "@/services/validarTamanhoArquivo";
import { construirArquivoFormData } from "@/services/construirArquivoFormData";

import type { Anexo } from "../types";

const ANEXOS_POR_PAGINA = 10;

type AnexosQueryResult = {
  anexos: Anexo[];
  pagination: ArquivoPagination | null;
};

export function useAnexos(pacienteId: string) {
  const [dataSelecionada, setDataSelecionadaState] = useState("");
  const [page, setPage] = useState(1);
  const [open, setOpen] = useState(false);

  const [reportToView, setReportToView] = useState<Anexo | null>(null);
  const [reportToDelete, setReportToDelete] = useState<Anexo | null>(null);

  const queryClient = useQueryClient();

  const { data, isLoading, isFetching } = useQuery<AnexosQueryResult>({
    queryKey: ["anexos", pacienteId, dataSelecionada, page],
    enabled: !!pacienteId,
    placeholderData: keepPreviousData,
    queryFn: async () => {
      
      if (dataSelecionada) {
        const anexos = await getAnexosPorData(pacienteId, dataSelecionada);
        return { anexos, pagination: null };
      }
      const resultado = await getAnexos(pacienteId, page, ANEXOS_POR_PAGINA);
      return { anexos: resultado.data, pagination: resultado.pagination };
    },
  });

  const anexosFiltrados = data?.anexos ?? [];
  const pagination = data?.pagination ?? null;

  useEffect(() => {
    if (
      pagination &&
      pagination.totalPages > 0 &&
      page > pagination.totalPages
    ) {
      setPage(pagination.totalPages);
    }
  }, [pagination, page]);

  const setDataSelecionada = (valor: string) => {
    setDataSelecionadaState(valor);
    setPage(1);
  };

  const enviarAnexoMutation = useMutation({
    mutationFn: enviarAnexo,
    onSuccess: () => {
      toast.success("Anexo criado com sucesso!");
      queryClient.invalidateQueries({ queryKey: ["anexos", pacienteId] });
      setOpen(false);
      setPage(1);
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
      
      const titulo = formData.titulo.trim().replace(/\s+/g, " ");
      const descricao = formData.descricao?.trim().replace(/\s+/g, " ") || "";
      
      const tituloRegex = /^(?=.*[\p{L}\p{M}])[\p{L}\p{M}0-9 \-:/()']*$/u;

      if (!tituloRegex.test(titulo)) {
        throw new Error("Título inválido");
      }
      
      const descRegex = /^(?=.*[\p{L}\p{M}])[\p{L}\p{M}0-9 \-:/()'%&#]*$/u;

      if (!descRegex.test(descricao)) {
        throw new Error("Descrição inválida");
      }
      
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
    anexos: data?.anexos ?? [],
    loading: isLoading,
    isFetching,
    anexosFiltrados,

    pagination,
    page,
    setPage,

    dataSelecionada,
    setDataSelecionada,

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