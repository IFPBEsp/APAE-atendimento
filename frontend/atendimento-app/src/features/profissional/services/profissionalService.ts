import { api } from "@/services/axios";
import dados from "../../../../data/verificacao.json";
import { Profissional } from "../types";

export async function getProfissional(): Promise<Profissional> {
  const { data } = await api.get<Profissional>(
    `/profissionais/${dados.idProfissional}`
  );

  return data;
}

export async function getPrimeiroNomeProfissional(): Promise<string> {
  const { data } = await api.get<string>(
    `/profissionais/${dados.idProfissional}/primeiro-nome`
  );

  return data;
}