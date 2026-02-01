import { api } from "@/services/axios";

import {
  Atendimento,
  AtendimentoPayload,
  AtendimentoGroupResponse,
} from "../types";

export async function getAtendimentos(
  pacienteId: string
): Promise<AtendimentoGroupResponse[]> {
  const { data } = await api.get<AtendimentoGroupResponse[]>(
    `/atendimentos/${pacienteId}`
  );

  return data;
}

export async function criarAtendimento(
  payload: AtendimentoPayload
): Promise<Atendimento> {
  const { data } = await api.post<Atendimento>("/atendimentos", payload);

  return data;
}

export async function editarAtendimento(
  atendimentoId: string,
  payload: AtendimentoPayload
): Promise<Atendimento> {
  const { data } = await api.put<Atendimento>(
    `/atendimentos/${atendimentoId}`,
    payload
  );

  return data;
}
