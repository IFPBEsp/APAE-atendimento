import { useMutation, useQueryClient } from "@tanstack/react-query";
import { criarAgendamento } from "../services/agendaService";

export function useCriarAgendamento() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: criarAgendamento,
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({
        queryKey: ["agendamentos"],
      });
    },
  });
}
