import { keepPreviousData, useQuery } from "@tanstack/react-query";

import { listarAgendamentos } from "../services/agendaService";
import { ListarAgendamentosParams } from "../types";
import { normalizarAgendamentos } from "../utils/normalizarAgendamento";

export function useAgendamentos(
  params: ListarAgendamentosParams = {},
) {
  return useQuery({
    queryKey: ["agendamentos", params],
    queryFn: () => listarAgendamentos(params),
    placeholderData: keepPreviousData,
    select: (data) => ({
      agendamentos: normalizarAgendamentos(data.content),
      pagination: data.pagination,
    }),
  });
}