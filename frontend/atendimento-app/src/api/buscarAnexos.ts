import dados from "../../data/verificacao.json";
import { TipoArquivo } from "@/components/forms/anexoForm";
import { AnexoResponse } from "@/types/Anexo";
export async function buscarAnexos(idPaciente: string, tipoArquivo: TipoArquivo) : Promise<AnexoResponse[]> {
    try{
const res = await fetch(`${dados.urlBase}/arquivo/list/${dados.idProfissional}/${idPaciente}/${tipoArquivo}`);

if(!res.ok){
  throw new Error('Erro ao buscar anexos');
}

const dto = await res.json() as AnexoResponse[];
const result : AnexoResponse[] = dto.map(e => {
        const {titulo, descricao, data, presignedUrl,nomeArquivo, objectName} = e;
        return {
             titulo,
             descricao,
             data,
             nomeArquivo,
             presignedUrl,
             objectName
        }       
})
return result;
    }catch(error){
        console.error("Erro ao buscar anexos:", error);
        throw error;
    }
}