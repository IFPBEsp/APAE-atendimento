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
  atendimentoId: string;
  pacienteId: string;
  nomePaciente: string;
  profissionalId: string;
  nomeProfissional: string;
  data: string;
  time: string;
  numeroAtendimento: string;
  status: boolean;
  externo?: boolean;
}

export interface DiaAgendamento {
  data: string;
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
