import dados from "../../data/verificacao.json";

export async function getPrimeiroNome(token: string): Promise<string> {
  const res = await fetch(
    `${dados.urlBase}/profissionais/${dados.idProfissional}/primeiro-nome`,
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );

  if (!res.ok) {
    throw new Error("Erro ao capturar nome");
  }

  return res.text();
}