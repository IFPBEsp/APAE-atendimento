import { api } from "@/services/axios";
import dados from "../../../../data/verificacao.json";
import { CriarAgendamentoPayload, PacienteOption } from "../types";

export async function listarAgendamentos(profissionalId: string) {
  const { data } = await api.get(`/agendamento/${profissionalId}`);
  return data;
}

export async function criarAgendamento(payload: CriarAgendamentoPayload) {
  const { data } = await api.post("/agendamento", payload);
  return data;
}

export async function deletarAgendamento(
  profissionalId: string,
  pacienteId: string,
  agendamentoId: string,
): Promise<void> {
  await api.delete(
    `/agendamento/${profissionalId}/${pacienteId}/${agendamentoId}`,
  );
}

export async function getPacientesPorProfissional(): Promise<PacienteOption[]> {
  const { data } = await api.get<PacienteOption[]>(
    `/profissionais/${dados.idProfissional}/pacientes-option`,
  );

  return data;
}
