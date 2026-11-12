export interface Relatorio {
  id?: string;
  titulo: string;
  descricao: string;
}

export interface Atendimento {
  id: string;
  data: string;
  hora: string;
  numeracao: string;
  status: boolean;
  relatorio: Relatorio[];
}

export interface AtendimentoPayload {
  pacienteId: string;
  data: string;
  hora: string;
  numeracao?: string;
  relatorio: Omit<Relatorio, "id">[];
}

export interface AtendimentoGroupResponse {
  atendimentos: Atendimento[];
}

export interface AtendimentoPageResponse {
  content: AtendimentoGroupResponse[];
  totalPages: number;
  totalElements: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}