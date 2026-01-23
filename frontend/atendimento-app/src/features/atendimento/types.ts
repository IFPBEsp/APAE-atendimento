export interface Relatorio {
  id?: string;
  titulo: string;
  descricao: string;
}

export interface Atendimento {
  id: string;
  data: string;
  hora: string;
  numeracao: number;
  relatorio: Relatorio[];
}

export interface AtendimentoPayload {
  profissionalId: string;
  pacienteId: string;
  data: string;
  hora: string;
  numeracao?: number;
  relatorio: Omit<Relatorio, "id">[];
}

export interface AtendimentoGroupResponse {
  atendimentos: Atendimento[];
}
