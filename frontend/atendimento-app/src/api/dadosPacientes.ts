import { Paciente } from "@/types/Paciente";
import dados from "../../data/verificacao.json";

export async function getPacientes(): Promise<Paciente[]>{
const res = await fetch(`${dados.urlBase}/profissionais/${dados.idProfissional}/pacientes`);
if(!res.ok){
    throw new Error('Erro ao buscar pacientes');
}
const data = await res.json(); 
return data;
}