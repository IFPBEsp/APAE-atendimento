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

export interface PaginationMeta {
  page: number;
  limit: number;
  totalItems: number;
  totalPages: number;
  hasNextPage: boolean;
  hasPreviousPage: boolean;
}

export type PaginatedResponse<T> = {
  data: T[];
  pagination: PaginationMeta;
}