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
        profissionalId: a.profissionalId || "",
        nomeProfissional: a.nomeProfissional || "Não informado",
        horario: a.time.substring(0, 5), 
        data: isoParaBR(a.data),
        numeracao: String(a.numeroAtendimento ?? "0"),
        status: a.status,
        externo: a.externo ?? false, 
      });
    });
  });

  return flatten;
}