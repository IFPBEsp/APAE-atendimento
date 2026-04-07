import { useQuery } from "@tanstack/react-query";
import { listarPacientesDropdown } from "@/services/pacienteService";

export function usePacientesDropdown() {
    return useQuery({
        queryKey: ['pacientes-dropdown'],
        queryFn: listarPacientesDropdown,
        staleTime: 1000 * 60 * 5,
    });
}