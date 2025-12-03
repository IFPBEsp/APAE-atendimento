import dados from "../../data/verificacao.json";

export async function enviarAnexo(anexoEnvio : FormData)  {
    try{
        const response = await fetch(`${dados.urlBase}/arquivo`, {
            method: "POST",
            body: anexoEnvio
        });

        if(!response.ok){
            throw new Error("Erro ao enviar anexo");
        }

        return await response.text();
    }catch(erro){
        console.error("Erro na requisição de envio de anexo:", erro);
        throw erro;
    }
} 