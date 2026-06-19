import {api} from './axios';
import { Dropdown } from "@/types/dropdown";

type PacienteApi = {
    id: string;
    nomeCompleto: string;
};

type PacientesApiResponse = {
    data: PacienteApi[];
};

export const listarPacientesDropdown = async(): Promise<Dropdown[]> => {
    const {data} = await api.get('/pacientes/dropdown');
    return data;
};

export const listarTodosPacientesDropdown = async(): Promise<Dropdown[]> => {
    const {data} = await api.get<PacientesApiResponse>('/pacientes/todos/search?limit=100');

    return data.data.map((paciente) => ({
        id: paciente.id,
        nome: paciente.nomeCompleto,
    }));
};
