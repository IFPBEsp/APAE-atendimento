import { api } from "@/services/axios";
import dados from "../../../../data/verificacao.json";
import { Paciente } from "../types";

export type FiltroPaciente = {
  nome?: string;
  cpf?: string;
  cidade?: string;
};

export async function getPacientes(
  filtros?: FiltroPaciente
): Promise<Paciente[]> {
  if (filtros && Object.keys(filtros).length > 0) {
    const query = new URLSearchParams(
      filtros as Record<string, string>
    ).toString();

    const { data } = await api.get<Paciente[]>(
      `/pacientes/search?${query}`
    );

    return data;
  }

  const { data } = await api.get<Paciente[]>(
    `/profissionais/${dados.idProfissional}/pacientes`
  );

  return data;
}
