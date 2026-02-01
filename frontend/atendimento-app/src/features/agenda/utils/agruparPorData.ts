import { Agendamento } from "../types";

export function agruparPorData(lista: Agendamento[]) {
  const grupos: Record<string, Agendamento[]> = {};

  lista.forEach((item) => {
    const [ano, mes, dia] = item.data.split("-");
    const label = `${dia}/${mes}/${ano}`;

    if (!grupos[label]) grupos[label] = [];
    grupos[label].push(item);
  });

  return grupos;
}
