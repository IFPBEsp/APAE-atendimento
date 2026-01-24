export interface Paciente {
  id: string;
  nomeCompleto: string;
  cpf: string;
  endereco: string;
  contato: string;
  dataNascimento: string;
  transtornos: string[];
  responsaveis: string[];
  fotoPreAssinada: string;
}