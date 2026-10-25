"use client";

import {
  Relatorio,
  RelatorioEnvioFormData,
} from "@/features/relatorio/types";

import { toast } from "sonner";
import { validarTamanhoArquivo } from "@/services/validarTamanhoArquivo";
import { construirArquivoFormData } from "@/services/construirArquivoFormData";
import { apagarAnexo } from "@/features/anexo/services/anexoService";

import { AxiosError } from "axios";
import {
  enviarRelatorio,
  getRelatorios,
  getRelatoriosPorData,
} from "@/features/relatorio/services/relatorioService";
import {
  keepPreviousData,
  useQuery,
  useMutation,
  useQueryClient,
} from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { TipoArquivo } from "@/features/arquivo/types";
import type { ArquivoPagination } from "@/features/arquivo/types";

import {
  normalizar,
  validarTexto,
  validarDataISO,
  validarArquivo,
} from "@/features/relatorio/utils/sanitizeRelatorio";

const RELATORIOS_POR_PAGINA = 10;

type RelatoriosQueryResult = {
  relatorios: Relatorio[];
  pagination: ArquivoPagination | null;
};

export function useRelatorios(pacienteId: string) {
  const [dataSelecionada, setDataSelecionadaState] = useState<string>("");
  const [page, setPage] = useState(1);
  const [open, setOpen] = useState(false);
  const [reportToDelete, setReportToDelete] = useState<Relatorio | null>(null);
  const [reportToView, setReportToView] = useState<Relatorio | null>(null);
  const queryClient = useQueryClient();

  const { data, isLoading, isFetching } = useQuery<RelatoriosQueryResult>({
    queryKey: ["relatorios", pacienteId, dataSelecionada, page],
    enabled: !!pacienteId,
    placeholderData: keepPreviousData,
    queryFn: async () => {
      
      if (dataSelecionada) {
        const lista = await getRelatoriosPorData(pacienteId, dataSelecionada);
        return {
          relatorios: lista.map((e, i) => ({ id: i + 1, ...e })),
          pagination: null,
        };
      }

      const resposta = await getRelatorios(
        pacienteId,
        page,
        RELATORIOS_POR_PAGINA,
      );
      const deslocamento =
        (resposta.pagination.page - 1) * resposta.pagination.limit;

      return {
        relatorios: resposta.data.map((e, i) => ({
          id: deslocamento + i + 1,
          ...e,
        })),
        pagination: resposta.pagination,
      };
    },
  });

  const relatoriosFiltrados = data?.relatorios ?? [];
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

  const enviarRelatorioMutation = useMutation({
    mutationFn: enviarRelatorio,
    onSuccess: () => {
      toast.success("Relatório criado com sucesso!");
      queryClient.invalidateQueries({
        queryKey: ["relatorios", pacienteId],
      });
      setPage(1);
    },
    onError: (error: unknown) => {
      let mensagem: string;

      if (error instanceof AxiosError) {
        const payload = error.response?.data;
        mensagem =
          payload && typeof payload === "object"
            ? JSON.stringify(payload)
            : payload || error.message;
      } else if (error instanceof Error) {
        mensagem = error.message;
      } else {
        mensagem = String(error);
      }

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

  function criarArquivoRelatorio(
    data: RelatorioEnvioFormData,
  ): FormData | undefined {
    if (!pacienteId) {
      toast.error("Paciente não selecionado.");
      return;
    }
    try {
      const titulo = normalizar(data.titulo);
      const descricao = normalizar(data.descricao);

      validarTexto(titulo, descricao);
      validarDataISO(data.data);
      validarTamanhoArquivo(data.arquivo);

      const arquivoValidado = data.arquivo?.[0]
        ? validarArquivo(data.arquivo[0])
        : undefined;

      const fileList = arquivoValidado
        ? ({
            0: arquivoValidado,
            length: 1,
            item: () => arquivoValidado,
            [Symbol.iterator]: function* () {
              yield arquivoValidado;
            },
          } as unknown as FileList)
        : data.arquivo;

      return construirArquivoFormData({
        ...data,
        pacienteId,
        tipoArquivo: TipoArquivo.relatorio,
        titulo,
        descricao,
        arquivo: fileList,
      });
    } catch (error) {
      const mensagem = error instanceof Error ? error.message : String(error);
      toast.error(mensagem || "Erro ao enviar anexo");
      return undefined;
    }
  }

  async function construirEnviarArquivoRelatorio(data: RelatorioEnvioFormData) {
    const relatorio = criarArquivoRelatorio(data);

    if (relatorio) {
      enviarRelatorioMutation.mutate(relatorio);
    }
  }

  const handleDelete = (objectName: string) => {
    deletarRelatorioMutation.mutate(objectName);
  };

  return {
    // dados-estados
    loading: isLoading,
    isFetching,
    dataSelecionada,
    open,
    reportToDelete,
    reportToView,
    relatoriosFiltrados,
    pagination,
    page,

    // ações-passivas
    setDataSelecionada,
    setPage,
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