import { api } from "@/services/axios";
import { Profissional } from "../types";

export async function getProfissional(): Promise<Profissional> {
  const { data } = await api.get<Profissional>(
    `/profissionais`
  );

  return data;
}

export async function getPrimeiroNomeProfissional(): Promise<string> {
  const { data } = await api.get<string>(
    `/profissionais/primeiro-nome`
  );

  return data;
}