import { getPacientes } from "../../home/services/homeService";
import { formatarData } from "@/utils/formatarData";
import { getAtendimentos } from "../services/atendimentoService";
import { Atendimento } from "../types";

export default function normalizarAtendimento(item: Atendimento): Atendimento {
  const [hora = "00", minuto = "00"] = (item.hora ?? "00:00").split(":");

  return {
    id: item.id,
    data: formatarData(item.data),
    hora: `${hora}:${minuto}`,
    numeracao: item.numeracao ?? 1,
    relatorio: item.relatorio,
    status: item.status ?? false,
  };
}

export async function carregarAtendimentos(
  pacienteId: string,
  page = 0,
  size = 10
) {
  const [pacientes, pagina] = await Promise.all([
    getPacientes(),
    getAtendimentos(pacienteId, page, size),
  ]);

  const paciente =
    pacientes.data.find((p) => String(p.id) === String(pacienteId)) ?? null;

  const atendimentos = pagina.content
    .flatMap((grupo) => grupo.atendimentos)
    .map(normalizarAtendimento);

  return {
    paciente,
    atendimentos,
    totalPages: pagina.totalPages,
    totalElements: pagina.totalElements,
    currentPage: pagina.number,
    pageSize: pagina.size,
    first: pagina.first,
    last: pagina.last,
  };
}