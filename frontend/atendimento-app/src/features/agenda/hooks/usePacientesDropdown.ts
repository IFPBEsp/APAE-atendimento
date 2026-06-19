import { useQuery } from "@tanstack/react-query";
import {
    listarPacientesDropdown,
    listarTodosPacientesDropdown,
} from "@/services/pacienteService";

export type OrigemPacientes = "meus" | "todos";

export function usePacientesDropdown(origem: OrigemPacientes = "meus") {
    return useQuery({
        queryKey: ['pacientes-dropdown', origem],
        queryFn: origem === "todos" ? listarTodosPacientesDropdown : listarPacientesDropdown,
        staleTime: 1000 * 60 * 5,
    });
}
