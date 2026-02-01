import { useMutation, useQueryClient } from "@tanstack/react-query";
import { deletarAgendamento } from "../services/agendaService";

export function useDeletarAgendamento() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      profissionalId,
      pacienteId,
      agendamentoId,
    }: {
      profissionalId: string;
      pacienteId: string;
      agendamentoId: string;
    }) => deletarAgendamento(profissionalId, pacienteId, agendamentoId),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({
        queryKey: ["agendamentos", variables.profissionalId],
      });
    },
  });
}
