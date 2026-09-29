import { api } from "@/services/axios";

import {
  Atendimento,
  AtendimentoPayload,
  AtendimentoPageResponse,
} from "../types";

export async function getAtendimentos(
  pacienteId: string,
  page = 0,
  size = 10
): Promise<AtendimentoPageResponse> {
  const { data } = await api.get<AtendimentoPageResponse>(
    `/atendimentos/${pacienteId}`,
    {
      params: {
        page,
        size,
      },
    }
  );

  return data;
}

export async function criarAtendimento(
  payload: AtendimentoPayload
): Promise<Atendimento> {
  const { data } = await api.post<Atendimento>("/atendimentos", payload);

  return data;
}

export async function deletarAtendimento(
  pacienteId: string,
  atendimentoId: string
): Promise<void> {
  await api.delete(`/atendimentos/${pacienteId}/${atendimentoId}`);
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

export const concluirAtendimento = async (
  atendimentoId: string
): Promise<void> => {
  await api.patch(`/atendimentos/${atendimentoId}/concluir`);
};