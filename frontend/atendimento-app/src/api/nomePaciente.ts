import dados from "../../data/verificacao.json";

export async function obterNomePaciente(idPaciente?: string) : Promise<string>{
try{
    const resposta = await fetch(`${dados.urlBase}/pacientes/${idPaciente}/nome-completo`);
    if(!resposta.ok){
        throw new Error("Erro ao retornar nome do paciente");
    } 
    return resposta.text();
}catch(error){
    let mensagem;
    if(error instanceof Error){
        mensagem = error.message;
    }
    throw mensagem || "";
}



}