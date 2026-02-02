export interface Paciente {
  id: string;
  nomeCompleto: string;
  cpf: string;
  endereco: string;
  contato: string;
  dataDeNascimento: string;
  transtornos: string[];
  responsaveis: string[];
  fotoPreAssinada: string;
}
