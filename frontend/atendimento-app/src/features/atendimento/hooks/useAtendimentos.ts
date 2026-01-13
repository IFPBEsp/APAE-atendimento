import { useQuery } from "@tanstack/react-query";
import { getAtendimentos } from "../services/atendimentoService";
import { getPacientes } from "@/api/dadosPacientes";
import agruparPorMes from "../utils/agruparAtendimentosPorMes";
import { Atendimento } from "../types";
import { formatarData } from "@/utils/formatarData";
import { useMemo, useState } from "react";

export function useAtendimentos(pacienteId: string) {
  const [dataSelecionada, setDataSelecionada] = useState("");
  const [open, setOpen] = useState(false);

  const { data, isLoading } = useQuery({
    queryKey: ["atendimentos", pacienteId],
    enabled: !!pacienteId,
    queryFn: async () => {
      const [pacientes, raw] = await Promise.all([
        getPacientes(),
        getAtendimentos(pacienteId),
      ]);

      const paciente = pacientes.find(
        (p) => String(p.id) === String(pacienteId)
      );

      const todos = raw.flatMap((grupo) => grupo.atendimentos);

      const atendimentos: Atendimento[] = todos.map((item) => {
        const [hora, minuto] = (item.hora ?? "00:00").split(":");

        return {
          id: item.id,
          data: formatarData(item.data),
          hora: `${hora}:${minuto}`,
          numeracao: item.numeracao ?? 1,
          relatorio: item.relatorio,
        };
      });

      return { paciente: paciente ?? null, atendimentos };
    },
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

  return {
    paciente: data?.paciente ?? null,
    atendimentos: data?.atendimentos ?? [],
    loading: isLoading,
    dataSelecionada,
    setDataSelecionada,
    atendimentosFiltrados,
    atendimentosAgrupados: agruparPorMes(atendimentosFiltrados),
    open,
    setOpen,
  };
}
