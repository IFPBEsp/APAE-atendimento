import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";

import { concluirAgendamento } from "../services/agendaService";

export function useConcluirAgendamento() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      pacienteId,
      agendamentoId,
    }: {
      pacienteId: string;
      agendamentoId: string;
    }) => concluirAgendamento(pacienteId, agendamentoId),
    onSuccess: () => {
      toast.success("Agendamento concluído.");
      queryClient.invalidateQueries({ queryKey: ["agendamentos"] });
    },
    onError: () => {
      toast.error("Erro ao concluir agendamento.");
    },
  });
}
