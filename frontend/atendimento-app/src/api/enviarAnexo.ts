import { Anexo } from "@/types/Anexo";
import dados from "../../data/verificacao.json";

export async function enviarAnexo(anexoEnvio : FormData) : Promise<Anexo> {
    try{

        const response = await fetch(`${dados.urlBase}/arquivo/upload`, {
            method: "POST",
            body: anexoEnvio
        });

        if(!response.ok){
             const erroServidor = await response.text(); 
             throw new Error(erroServidor || "Erro ao enviar anexo");
        }
         
        const dto = await response.json();
        const {titulo, descricao, data,objectName, presignedUrl} = dto;
        const result : Anexo = {
             titulo,
             descricao,
             data,
             fileName: objectName,
             imageUrl: presignedUrl
        }

        return result;

    }catch(erro){
        console.error("Erro na requisição de envio de anexo:", erro);
        throw erro;
    }
} 