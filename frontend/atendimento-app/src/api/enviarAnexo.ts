import { Anexo } from "@/types/Anexo";
import dados from "../../data/verificacao.json";

export async function enviarAnexo(anexoEnvio : FormData) : Promise<Anexo> {
    try{

        const response = await fetch(`${dados.urlBase}/arquivo/upload`, {
            method: "POST",
            body: anexoEnvio
        });

        if(!response.ok){
            throw new Error("Erro ao enviar anexo");
        }
         
        const dto = await response.json();
        const result : Anexo = {
             titulo: dto.titulo,
             descricao: dto.descricao,
             data: dto.data,
             fileName: dto.objectName,
             imageUrl: dto.presignedUrl
        }
        //console.log(result);

        return result;

    }catch(erro){
        console.error("Erro na requisição de envio de anexo:", erro);
        throw erro;
    }
} 