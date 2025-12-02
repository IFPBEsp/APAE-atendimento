import dados from "../../data/verificacao.json";

export async function getPrimeiroNome(): Promise<string> {
  const res = await fetch(
    `${dados.urlBase}/profissionais/${dados.idProfissional}/primeiro-nome`
  );
  if (!res.ok) {
    throw new Error("Erro ao capturar o nome do profissional");
  }
  const data = await res.text();
  return data;
}
