import dados from "../../data/verificacao.json";

export async function getPacientes(token: string) {
  const res = await fetch(`${dados.urlBase}/profissionais/${dados.idProfissional}/pacientes`, {
    headers: {
      "Authorization": `Bearer ${token}`
    }
  });

  if (!res.ok) throw new Error("Erro na API Java");

  return res.json();
}