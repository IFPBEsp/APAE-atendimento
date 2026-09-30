import { useQuery } from "@tanstack/react-query";
import agruparPorMes from "../utils/agruparAtendimentosPorMes";
import { useMemo, useState } from "react";
import { Atendimento } from "../types";
import { carregarAtendimentos } from "../utils/normalizarAtendimento";

export function useAtendimentos(pacienteId: string) {
  const [dataSelecionada, setDataSelecionada] = useState("");
  const [open, setOpen] = useState(false);
  const [paginaAtual, setPaginaAtual] = useState(0);

  const { data, isLoading } = useQuery({
    queryKey: ["atendimentos", pacienteId, paginaAtual],
    enabled: !!pacienteId,
    queryFn: () => carregarAtendimentos(pacienteId, paginaAtual, 10),
  });

  const dataFormatada = dataSelecionada
    ? dataSelecionada.split("-").reverse().join("/")
    : "";

  const atendimentosFiltrados = useMemo(() => {
    if (!data?.atendimentos) return [];

    return dataSelecionada
      ? data.atendimentos.filter((a: Atendimento) => a.data === dataFormatada)
      : data.atendimentos;
  }, [data, dataSelecionada, dataFormatada]);

  const atendimentosAgrupados = useMemo(
    () => agruparPorMes(atendimentosFiltrados),
    [atendimentosFiltrados],
  );

  const irParaPaginaAnterior = () => {
    if (!data?.first) {
      setPaginaAtual((pagina) => pagina - 1);
    }
  };

  const irParaProximaPagina = () => {
    if (!data?.last) {
      setPaginaAtual((pagina) => pagina + 1);
    }
  };

  return {
    paciente: data?.paciente ?? null,
    atendimentos: data?.atendimentos ?? [],
    atendimentosFiltrados,
    atendimentosAgrupados,
    loading: isLoading,

    dataSelecionada,
    setDataSelecionada,

    open,
    setOpen,

    paginaAtual,
    totalPaginas: data?.totalPages ?? 0,
    irParaPaginaAnterior,
    irParaProximaPagina,
    primeiraPagina: data?.first ?? true,
    ultimaPagina: data?.last ?? true,
  };
}