export interface Paciente{
    id: string,
    nome: string,
    cpf: string,
    dataNascimento: string,
    contato: string,
    transtornos: string[],
    responsaveis: string[],
    rua:string,
    bairro: string,
    numeroCasa: string,
    cidade: string
}
