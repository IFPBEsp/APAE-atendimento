import { api } from "@/services/axios";
import {Paciente, PaginationMeta} from "../types";
import { PaginatedResponse } from "../types";

export type FiltroPaciente = {
  nome?: string;
  cpf?: string;
  cidade?: string;
  page?: number;
  limit?: number;
};

export type OrigemPacientes = "meus" | "todos";

type PacientesApiResponse = {
  data: Paciente[];
  paginationMetaDTO: PaginationMeta;
};

export async function getPacientes(
  filtros: FiltroPaciente = { page: 1, limit: 10 },
  origem: OrigemPacientes = "meus"
): Promise<PaginatedResponse<Paciente>> {

  const params = new URLSearchParams();
  Object.entries(filtros).forEach(([key, value]) => {
    if (value) params.append(key, String(value));
  });

  const { data } = await api.get<PacientesApiResponse>(
      `/pacientes/${origem === "todos" ? "todos/search" : "search"}?${params.toString()}`
  );

  return {
    data: data.data,
    pagination: data.paginationMetaDTO
  };
}
