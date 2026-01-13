import { Atendimento } from "../types";

export default function agruparPorMes(lista: Atendimento[]) {
  const meses: Record<string, Atendimento[]> = {};

  lista.forEach((item) => {
    const [, mes, ano] = item.data.split("/");

    const nomeMes = new Date(Number(ano), Number(mes) - 1).toLocaleString(
      "pt-BR",
      { month: "long" }
    );

    const chave = `${nomeMes} ${ano}`;

    if (!meses[chave]) meses[chave] = [];
    meses[chave].push(item);
  });

  return meses;
}
