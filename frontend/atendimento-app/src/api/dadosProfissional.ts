import { Profissional } from "@/types/Profissional";
import dados from "../../data/verificacao.json";

export async function getProfissional(
  profissionalId: string
): Promise<Profissional> {
  const res = await fetch(`${dados.urlBase}/profissionais/${profissionalId}`, {
    method: "GET",
  });

  if (!res.ok) {
    throw new Error("Erro ao buscar profissional");
  }

  return res.json();
}
