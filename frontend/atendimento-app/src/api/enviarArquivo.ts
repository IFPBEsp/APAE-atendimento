import dados from "../../data/verificacao.json";

export async function enviarArquivo(relatorioEnvio : FormData)  {
    try{
        const response = await fetch(`${dados.urlBase}/arquivo`, {
            method: "POST",
            body: relatorioEnvio
        });

        if(!response.ok){
             const erroServidor = await response.text(); 
             throw new Error(erroServidor || "Erro ao enviar relatorio");
        }

    }catch(erro){
        console.error("Erro na requisição de envio de relatorio:", erro);
        throw erro;
    }
} 