import { useQuery } from "@tanstack/react-query";
import { normalizarAgendamentos } from "../utils/normalizarAgendamento";
import { listarAgendamentos } from "../services/agendaService";

export function useAgendamentos() {
  return useQuery({
    queryKey: ["agendamentos"],
    queryFn: () => listarAgendamentos(),
    select: normalizarAgendamentos,
  });
}
