import { Atendimento } from "../types";

export function calcularNumeracao(
  atendimentos: Atendimento[],
  dataISO: string,
  offset: number = 0
): string {
  if (!dataISO) return "1";
  const [ano, mes] = dataISO.split("-");
  const atendimentosDoMes = atendimentos.filter((a) => {
    const [, mesBR, anoBR] = a.data.split("/");
    return mesBR === mes && anoBR === ano;
  });
  return String(atendimentosDoMes.length + 1 + offset);
}