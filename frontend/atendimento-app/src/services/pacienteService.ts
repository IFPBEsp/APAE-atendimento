import {api} from './axios';
import { Dropdown } from "@/types/dropdown";

export const listarPacientesDropdown = async(): Promise<Dropdown[]> => {
    const {data} = await api.get('/pacientes/dropdown');
    return data;
};