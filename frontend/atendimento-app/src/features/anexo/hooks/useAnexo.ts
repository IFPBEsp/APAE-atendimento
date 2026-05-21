import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useMemo, useState } from "react";
import { apagarAnexo, enviarAnexo, getAnexos } from "../services/anexoService";
import { toast } from "sonner";

import { TipoArquivo } from "@/features/anexo/types";
import type { AnexoEnvioFormData } from "@/features/anexo/types";

import { validarTipoArquivo } from "@/services/validarTipoArquivo";
import { validarTamanhoArquivo } from "@/services/validarTamanhoArquivo";
import { construirArquivoFormData } from "@/services/construirArquivoFormData";

import type { Anexo } from "../types";
import { filtroData } from "@/utils/filtroData";

export function useAnexos(pacienteId: string) {
  const [dataSelecionada, setDataSelecionada] = useState("");
  const [open, setOpen] = useState(false);

  const [reportToView, setReportToView] = useState<Anexo | null>(null);
  const [reportToDelete, setReportToDelete] = useState<Anexo | null>(null);

  const queryClient = useQueryClient();

  const { data, isLoading } = useQuery<{ anexos: Anexo[] }>({
    queryKey: ["anexos", pacienteId],
    enabled: !!pacienteId,
    queryFn: async () => {
      const anexos = await getAnexos(pacienteId);
      return { anexos };
    },
  });

  const anexosFiltrados = useMemo(() => {
    if (!data?.anexos) return [];
    if (!dataSelecionada) return data.anexos;

    return data.anexos.filter((a: Anexo) =>
      filtroData(dataSelecionada, a.data),
    );
  }, [data, dataSelecionada]);

  const enviarAnexoMutation = useMutation({
    mutationFn: enviarAnexo,
    onSuccess: () => {
      toast.success("Anexo criado com sucesso!");
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
    anexos: data?.anexos ?? [],
    loading: isLoading,
    anexosFiltrados,

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
