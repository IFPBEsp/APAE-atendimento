export interface Agendamento {
  id: string;
  pacienteId: string;
  paciente: string;
  data: string;
  horario: string;
  numeracao: number;
  status: boolean;
}

export interface AgendamentoResponse {
  atendimentoId: string;
  pacienteId: string;
  nomePaciente: string;
  data: string;
  time: string;
  numeroAtendimento: number;
  status: boolean;
}

export interface DiaAgendamento {
  data: string;
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
