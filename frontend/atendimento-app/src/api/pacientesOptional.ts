import dados from "../../data/verificacao.json";

export type PacienteOption = {
  id: string;
  nome: string;
};

export async function getPacientesPorProfissional(
): Promise<PacienteOption[]> {
  const response = await fetch(
    `http://localhost:8080/profissionais/${dados.idProfissional}/pacientes-option`,
    {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    }
  );

  if (!response.ok) {
    throw new Error("Erro ao buscar pacientes do profissional");
  }

  return response.json();
}