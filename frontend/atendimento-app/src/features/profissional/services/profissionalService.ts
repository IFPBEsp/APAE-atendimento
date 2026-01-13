import { api } from "@/services/axios";
import dados from "../../../../data/verificacao.json";
import { Profissional } from "../types";

export async function getProfissional(): Promise<Profissional> {
  const { data } = await api.get<Profissional>(
    `/profissionais/${dados.idProfissional}`
  );

  return data;
}
