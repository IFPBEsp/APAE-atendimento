import { Paciente } from "@/types/Paciente";

const API_URL = process.env.NEXT_PUBLIC_API_URL;
export async function getPacientes(): Promise<Paciente[]>{
const res = await fetch(`${API_URL}/profissionais/c9cf649c-e6ef-486e-8809-2b400a8ba72b/pacientes`);
if(!res.ok){
    throw new Error('Erro ao buscar pacientes');
}
const data = await res.json(); 
  return data;
}

