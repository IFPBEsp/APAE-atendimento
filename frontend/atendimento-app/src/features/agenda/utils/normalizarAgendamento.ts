import { Agendamento, DiaAgendamento } from "../types";

function normalizarData(data: string): string {
  if (!data) return "";

  if (/^\d{2}-\d{2}-\d{4}$/.test(data)) {
    return data;
  }

  if (/^\d{4}-\d{2}-\d{2}$/.test(data)) {
    const [ano, mes, dia] = data.split("-");
    return `${dia}-${mes}-${ano}`;
  }

  return data;
}

function dataHoraParaOrdenacao(data: string, hora: string): number {
  const dataNormalizada = normalizarData(data);

  const [dia, mes, ano] = dataNormalizada.split("-");
  const [horas = "00", minutos = "00"] = hora.split(":");

  return new Date(
      Number(ano),
      Number(mes) - 1,
      Number(dia),
      Number(horas),
      Number(minutos)
  ).getTime();
}

export function normalizarAgendamentos(
    dto: DiaAgendamento[] = [],
): Agendamento[] {
  const flatten: Agendamento[] = [];

  dto.forEach((dia) => {
    dia.agendamentos?.forEach((a) => {
      flatten.push({
        id: a.id,
        pacienteId: a.pacienteId,
        paciente: a.nomePaciente,
        profissionalId: a.profissionalId || "",
        nomeProfissional: a.nomeProfissional || "Não informado",
        horario: a.hora?.substring(0, 5) || "",
        data: normalizarData(a.data || dia.dia),
        numeracao: a.numeracao ?? "0",
        status: a.status,
        externo: a.externo ?? false,
      });
    });
  });

  return flatten.sort((a, b) => {
    return dataHoraParaOrdenacao(a.data, a.horario) - dataHoraParaOrdenacao(b.data, b.horario);
  });
}