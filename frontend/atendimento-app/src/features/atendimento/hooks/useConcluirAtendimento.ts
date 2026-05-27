import { useMutation, useQueryClient } from '@tanstack/react-query';
import { concluirAtendimento } from '../services/atendimentoService';
import { toast } from 'sonner';

export const useConcluirAtendimento = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (atendimentoId: string) => concluirAtendimento(atendimentoId),
        onSuccess: () => {
            toast.success('Atendimento concluído com sucesso!');
            queryClient.invalidateQueries({ queryKey: ['atendimentos'] });
        },
        onError: () => {
            toast.error('Erro ao concluir o atendimento.');
        },
    });
};