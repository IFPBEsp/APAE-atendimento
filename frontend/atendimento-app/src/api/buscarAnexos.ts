import { Anexo } from "@/types/Anexo";
import dados from "../../data/verificacao.json";
export async function buscarAnexos(idPaciente: string, tipoArquivo: string) : Promise<Anexo[]> {
    try{
const res = await fetch(`${dados.urlBase}/arquivo/list/${dados.idProfissional}/${idPaciente}/${tipoArquivo}`);

if(!res.ok){
  throw new Error('Erro ao buscar anexos');
}

const data = await res.json();
return data;
    }catch(error){
        console.error("Erro ao buscar anexos:", error);
        return []; 
    }
}