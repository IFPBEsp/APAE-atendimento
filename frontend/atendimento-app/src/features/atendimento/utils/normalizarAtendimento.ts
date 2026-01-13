import { getPacientes } from "@/api/dadosPacientes";
import { formatarData } from "@/utils/formatarData";
import { getAtendimentos } from "../services/atendimentoService";
import { Atendimento } from "../types";

export default function normalizarAtendimento(item: Atendimento): Atendimento {
  const [hora = "00", minuto = "00"] = (item.hora ?? "00:00").split(":");

  return {
    id: item.id,
    data: formatarData(item.data),
    hora: `${hora}:${minuto}`,
    numeracao: item.numeracao ?? 1,
    relatorio: item.relatorio,
  };
}

export async function carregarAtendimentos(pacienteId: string) {
  const [pacientes, grupos] = await Promise.all([
    getPacientes(),
    getAtendimentos(pacienteId),
  ]);

  const paciente =
    pacientes.find((p) => String(p.id) === String(pacienteId)) ?? null;

  const atendimentos = grupos
    .flatMap((grupo) => grupo.atendimentos)
    .map(normalizarAtendimento);

  return { paciente, atendimentos };
}
