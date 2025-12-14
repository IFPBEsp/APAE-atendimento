import { ArquivoResponse } from "@/types/Arquivo";
import dados from "../../data/verificacao.json";
import { TipoArquivo } from "@/components/forms/anexoForm";

export async function buscarArquivos (idPaciente: string, tipoArquivo: TipoArquivo) : Promise<ArquivoResponse[]> {
    try{
const res = await fetch(`${dados.urlBase}/arquivo/list/${dados.idProfissional}/${idPaciente}/${tipoArquivo}`);

if(!res.ok){
  throw new Error('Erro ao buscar arquivo');
}

const dto = await res.json() as ArquivoResponse[];
return dto;
    }catch(error){
        console.error("Erro ao buscar arquivo:", error);
        throw error;
    }
}