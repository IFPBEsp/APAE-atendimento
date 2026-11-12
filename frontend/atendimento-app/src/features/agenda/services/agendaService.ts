import { api } from "@/services/axios";

import {
  AgendamentoPageResponse,
  AgendamentosPaginados,
  CriarAgendamentoPayload,
  ListarAgendamentosParams,
  PacienteOption,
} from "../types";

export async function listarAgendamentos(
  params: ListarAgendamentosParams = {},
): Promise<AgendamentosPaginados> {
  const {
    data,
    page = 1,
    size = 10,
  } = params;

  const queryParams = new URLSearchParams();

  if (data) {
    queryParams.append("data", data);
  }

  queryParams.append("page", String(Math.max(0, page - 1)));
  queryParams.append("size", String(size));

  const { data: response } = await api.get<AgendamentoPageResponse>(
    `/agendamento?${queryParams.toString()}`,
  );

  return {
    content: response.content ?? [],
    pagination: {
      page: response.number + 1,
      size: response.size,
      totalElements: response.totalElements,
      totalPages: response.totalPages,
      first: response.first,
      last: response.last,
    },
  };
}

export async function criarAgendamento(payload: CriarAgendamentoPayload) {
  const { data } = await api.post("/agendamento", payload);
  return data;
}

export async function editarAgendamento(
  agendamentoId: string,
  payload: CriarAgendamentoPayload,
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