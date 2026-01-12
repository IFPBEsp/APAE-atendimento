import { api } from "@/services/axios";
import dados from "../../../../data/verificacao.json";
import { Atendimento, AtendimentoPayload, AtendimentoApi } from "../types";

export async function getAtendimentos(
  pacienteId: string
): Promise<AtendimentoApi[]> {
  const { data } = await api.get<AtendimentoApi[]>(
    `/atendimentos/${dados.idProfissional}/${pacienteId}`
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
