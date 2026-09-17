export interface Agendamento {
  id: string;
  pacienteId: string;
  paciente: string;
  profissionalId: string;
  nomeProfissional: string;
  data: string;
  horario: string;
  numeracao: string;
  status: boolean;
  externo: boolean;
}

export interface AgendamentoResponse {
  id: string;
  pacienteId: string;
  nomePaciente: string;
  profissionalId?: string;
  nomeProfissional?: string;
  data: string;
  hora: string;
  numeracao: string;
  status: boolean;
  externo?: boolean;
}

export interface DiaAgendamento {
  dia: string;
  agendamentos: AgendamentoResponse[];
}

export type CriarAgendamentoPayload = {
  pacienteId: string;
  data: string;
  hora: string;
};

export type PacienteOption = {
  id: string;
  nome: string;
};

export type ListarAgendamentosParams = {
  data?: string;
  page?: number;
  size?: number;
};

export type AgendamentoPagination = {
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
};

export type AgendamentoPageResponse = {
  content: DiaAgendamento[];
  number: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
};

export type AgendamentosPaginados = {
  content: DiaAgendamento[];
  pagination: AgendamentoPagination;
};