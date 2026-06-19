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
  profissionalId: string;
  data: string;
  hora: string;
};

export type PacienteOption = {
  id: string;
  nome: string;
};
