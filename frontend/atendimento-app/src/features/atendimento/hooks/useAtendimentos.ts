import { useQuery } from "@tanstack/react-query";
import agruparPorMes from "../utils/agruparAtendimentosPorMes";
import { useMemo, useState } from "react";
import { carregarAtendimentos } from "../utils/normalizarAtendimento";

export function useAtendimentos(pacienteId: string) {
  const [dataSelecionada, setDataSelecionada] = useState("");
  const [open, setOpen] = useState(false);

  const { data, isLoading } = useQuery({
    queryKey: ["atendimentos", pacienteId],
    enabled: !!pacienteId,
    queryFn: () => carregarAtendimentos(pacienteId),
  });

  const dataFormatada = dataSelecionada
    ? dataSelecionada.split("-").reverse().join("/")
    : "";

  const atendimentosFiltrados = useMemo(() => {
    if (!data?.atendimentos) return [];

    return dataSelecionada
      ? data.atendimentos.filter((a) => a.data === dataFormatada)
      : data.atendimentos;
  }, [data, dataSelecionada, dataFormatada]);

  const atendimentosAgrupados = useMemo(
    () => agruparPorMes(atendimentosFiltrados),
    [atendimentosFiltrados]
  );

  return {
    paciente: data?.paciente ?? null,
    atendimentos: data?.atendimentos ?? [],
    atendimentosFiltrados,
    atendimentosAgrupados,
    loading: isLoading,

    dataSelecionada,
    setDataSelecionada,

    open,
    setOpen
  };
}
