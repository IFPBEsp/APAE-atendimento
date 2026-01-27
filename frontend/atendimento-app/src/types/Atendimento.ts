export interface Relatorio {
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
  relatorio: Record<string, string>;
}

export interface AtendimentoApi {
  id: string;
  data: string;
  hora: string;
  numeracao: number;
  relatorio: Record<string, string>;
}

export interface GrupoAtendimentosApi {
  mesAno: string;
  atendimentos: AtendimentoApi[];
}
