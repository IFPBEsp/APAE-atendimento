export interface Agendamento {
  id: string;
  pacienteNome: string;
  data: string;
  hora: string;
  numeroAtendimento: number;
}
export interface DiaAgendamento {
  data: string;
  agendamentos: Agendamento[];
}
