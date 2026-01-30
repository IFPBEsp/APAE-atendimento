import { isoParaBR } from "@/utils/formatarData";
import { Agendamento, AgendamentoResponse, DiaAgendamento } from "../types";

export function normalizarAgendamentos(
  dto: DiaAgendamento[] = [],
): Agendamento[] {
  const flatten: Agendamento[] = [];

  dto.forEach((dia) => {
    dia.agendamentos?.forEach((a: AgendamentoResponse) => {
      flatten.push({
        id: a.atendimentoId,
        pacienteId: a.pacienteId,
        paciente: a.nomePaciente,
        horario: a.time,
        data: isoParaBR(a.data),
        numeracao: a.numeroAtendimento ?? 0,
        status: a.status,
      });
    });
  });

  return flatten;
}
