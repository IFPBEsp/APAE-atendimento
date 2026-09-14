import { api } from "@/services/axios";

import { CriarAgendamentoPayload, PacienteOption } from "../types";

export async function listarAgendamentos() {
  const { data } = await api.get(`/agendamento?size=1000`);
  return data.content || data;
}

export async function criarAgendamento(payload: CriarAgendamentoPayload) {
  const { data } = await api.post("/agendamento", payload);
  return data;
}

export async function editarAgendamento(
  agendamentoId: string,
  payload: CriarAgendamentoPayload
) {
  const { data } = await api.put(`/agendamento/${agendamentoId}`, payload);
  return data;
}

export async function deletarAgendamento(
  pacienteId: string,
  agendamentoId: string,
): Promise<void> {
  await api.delete(
    `/agendamento/${pacienteId}/${agendamentoId}`,
  );
}

export async function concluirAgendamento(
  pacienteId: string,
  agendamentoId: string,
): Promise<void> {
  await api.patch(`/agendamento/${pacienteId}/${agendamentoId}/concluir`);
}

export async function getPacientesPorProfissional(): Promise<PacienteOption[]> {
  const { data } = await api.get<PacienteOption[]>(
    `/profissionais/pacientes-option`,
  );

  return data;
}
