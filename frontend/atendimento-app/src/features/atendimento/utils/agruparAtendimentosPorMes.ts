import { Atendimento } from "../types";

export default function agruparPorMes(lista: Atendimento[]) {
  const meses: Record<string, Atendimento[]> = {};

  const listaOrdenada = [...lista].sort((a, b) => {
    const [diaA, mesA, anoA] = a.data.split("/");
    const dataA = new Date(Number(anoA), Number(mesA) - 1, Number(diaA));
    const [diaB, mesB, anoB] = b.data.split("/");
    const dataB = new Date(Number(anoB), Number(mesB) - 1, Number(diaB));
    
    return dataB.getTime() - dataA.getTime();
  });

  listaOrdenada.forEach((item) => {
    const [, mes, ano] = item.data.split("/");

    const nomeMes = new Date(Number(ano), Number(mes) - 1).toLocaleString(
      "pt-BR",
      { month: "long" },
    );

    const chave = `${nomeMes} ${ano}`;

    if (!meses[chave]) meses[chave] = [];
    meses[chave].push(item);
  });

  return meses;
}
