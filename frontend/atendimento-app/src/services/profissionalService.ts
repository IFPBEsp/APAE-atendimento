import {api} from './axios';
import { Dropdown } from "@/types/dropdown";

export const listarProfissionaisDropdown = async (): Promise<Dropdown[]> => {
    const { data } = await api.get('/profissionais/dropdown');
    return data;
};