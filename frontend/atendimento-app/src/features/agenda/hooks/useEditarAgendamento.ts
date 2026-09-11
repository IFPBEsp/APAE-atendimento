import { useMutation, useQueryClient } from "@tanstack/react-query";
import { editarAgendamento } from "../services/agendaService";
import { CriarAgendamentoPayload } from "../types";

export function useEditarAgendamento() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      agendamentoId,
      payload,
    }: {
      agendamentoId: string;
      payload: CriarAgendamentoPayload;
    }) => editarAgendamento(agendamentoId, payload),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["agendamentos"],
      });
      queryClient.invalidateQueries({
        queryKey: ["pacientes"],
      });
      queryClient.invalidateQueries({
        queryKey: ["pacientes-dropdown"],
      });
    },
  });
}
