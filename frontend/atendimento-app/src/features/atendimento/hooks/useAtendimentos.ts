import { useEffect, useState } from "react";
import { Atendimento } from "../types";
import { getAtendimentos } from "../services/atendimentoService";
import { getPacientes } from "@/api/dadosPacientes";
import agruparPorMes from "../utils/agruparAtendimentosPorMes";
import { Paciente } from "@/types/Paciente";
import { formatarData } from "@/utils/formatarData";

export function useAtendimentos(pacienteId: string) {
  const [paciente, setPaciente] = useState<Paciente | null>(null);
  const [atendimentos, setAtendimentos] = useState<Atendimento[]>([]);
  const [dataSelecionada, setDataSelecionada] = useState("");
  const [loading, setLoading] = useState(true);
  const [open, setOpen] = useState(false);

  useEffect(() => {
    async function load() {
      if (typeof pacienteId !== "string") return;
      try {
        const pacientes = await getPacientes();
        const nomePaciente = pacientes.find(
          (p) => String(p.id) === String(pacienteId)
        );

        if (nomePaciente) {
          setPaciente(nomePaciente);
        }

        const raw = await getAtendimentos(pacienteId);

        const todos = raw.flatMap((grupo) => grupo.atendimentos);

        const convertidos: Atendimento[] = todos.map((item) => {
          const [hora, minuto] = item.hora.split(":");
          const horaFormatada = `${hora}:${minuto}`;

          return {
            id: item.id,
            data: formatarData(item.data),
            hora: horaFormatada,
            numeracao: item.numeracao ?? 1,
            relatorio: item.relatorio,
          };
        });

        setAtendimentos(convertidos);
      } catch (err) {
      } finally {
        setLoading(false);
      }
    }

    load();
  }, [pacienteId]);

  const dataFormatada = dataSelecionada
    ? dataSelecionada.split("-").reverse().join("/")
    : "";

  const atendimentosFiltrados = dataSelecionada
    ? atendimentos.filter((a) => a.data === dataFormatada)
    : atendimentos;

  return {
    paciente,
    atendimentos,
    loading,
    dataSelecionada,
    setDataSelecionada,
    atendimentosFiltrados,
    atendimentosAgrupados: agruparPorMes(atendimentosFiltrados),
    adicionarAtendimento: (novo: Atendimento) =>
      setAtendimentos((prev) => [novo, ...prev]),
    atualizarAtendimento: (atualizado: Atendimento) =>
      setAtendimentos((prev) =>
        prev.map((a) => (a.id === atualizado.id ? atualizado : a))
      ),
    open,
    setOpen,
  };
}
