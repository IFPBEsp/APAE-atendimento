import { useQuery } from "@tanstack/react-query";
import { listarProfissionaisDropdown } from "@/services/profissionalService";

export function useProfissionaisDropdown() {
    return useQuery({
        queryKey: ['profissionais-dropdown'],
        queryFn: listarProfissionaisDropdown,
        staleTime: 1000 * 60 * 5,
    });
}