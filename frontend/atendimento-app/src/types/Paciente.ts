export interface Paciente{
    id: string,
    nomeCompleto: string,
    dataNascimento: string,
    contato: string,
    transtornos: string[],
    responsaveis: string[],
    rua:string,
    bairro: string,
    cidade: string,
    numeroCasa: number
}