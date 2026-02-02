import { useMutation, useQueryClient } from "@tanstack/react-query";
import { deletarAgendamento } from "../services/agendaService";

export function useDeletarAgendamento() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      pacienteId,
      agendamentoId,
    }: {
      pacienteId: string;
      agendamentoId: string;
    }) => deletarAgendamento(pacienteId, agendamentoId),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["agendamentos"],
      });
    },
  });
}
