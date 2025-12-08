import { Anexo } from "@/types/Anexo";
import dados from "../../data/verificacao.json";
export async function buscarAnexos(idPaciente: string | undefined, tipoArquivo: number) : Promise<Anexo[]> {
    try{
const res = await fetch(`${dados.urlBase}/arquivo/list/${dados.idProfissional}/${idPaciente}/${tipoArquivo}`);

if(!res.ok){
  throw new Error('Erro ao buscar anexos');
}

const dto = await res.json();
const result : Anexo[] = dto.map(e => {
        const {titulo, descricao, data, presignedUrl,nomeArquivo, objectName} = e;
        return {
             titulo,
             descricao,
             data,
             fileName: nomeArquivo,
             imageUrl: presignedUrl,
             objectName
        }       
})
return result;
    }catch(error){
        console.error("Erro ao buscar anexos:", error);
        throw error;
    }
}