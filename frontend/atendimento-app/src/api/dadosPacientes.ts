import { Paciente } from "@/types/Paciente";
import dados from "../../data/verificacao.json";

type FiltroPaciente = {
  nome?: string;
  cpf?: string;
  cidade?: string;
};

export async function getPacientes(
  filtros?: FiltroPaciente
): Promise<Paciente[]> {

  let url = `${dados.urlBase}/profissionais/${dados.idProfissional}/pacientes`;

  if (filtros && Object.keys(filtros).length > 0) {
    const query = new URLSearchParams(filtros as Record<string, string>).toString();
    url = `${dados.urlBase}/pacientes/search?${query}`;
  }

  const res = await fetch(url, { cache: "no-store" });

  if (!res.ok) {
    throw new Error("Erro ao buscar pacientes");
  }

  return res.json();
}
