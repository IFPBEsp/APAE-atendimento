import dados from "../../data/verificacao.json";

export async function enviarAnexo(anexoEnvio : FormData)  {
    try{
        const response = await fetch(`${dados.urlBase}/arquivo/upload`, {
            method: "POST",
            body: anexoEnvio
        });

        if(!response.ok){
             const erroServidor = await response.text(); 
             throw new Error(erroServidor || "Erro ao enviar anexo");
        }

    }catch(erro){
        console.error("Erro na requisição de envio de anexo:", erro);
        throw erro;
    }
} 