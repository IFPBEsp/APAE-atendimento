import { Agendamento } from "../types";

export function agruparPorData(lista: Agendamento[]) {
  const grupos: Record<string, Agendamento[]> = {};

  lista.forEach((item) => {
    const parts = item.data.split("-");
    let ano: string, mes: string, dia: string;
    if (parts[0]?.length === 4) {
      [ano, mes, dia] = parts;
    } else {
      [dia, mes, ano] = parts;
    }
    const label = `${dia}/${mes}/${ano}`;

    if (!grupos[label]) grupos[label] = [];
    grupos[label].push(item);
  });

  return grupos;
}
