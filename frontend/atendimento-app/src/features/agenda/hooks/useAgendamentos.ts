import { useQuery } from "@tanstack/react-query";
import { normalizarAgendamentos } from "../utils/normalizarAgendamento";
import { listarAgendamentos } from "../services/agendaService";

export function useAgendamentos(profissionalId: string) {
  return useQuery({
    queryKey: ["agendamentos", profissionalId],
    queryFn: () => listarAgendamentos(profissionalId),
    select: normalizarAgendamentos,
  });
}
