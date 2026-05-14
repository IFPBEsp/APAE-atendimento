package br.org.apae.atendimento.entities.views;


import java.util.UUID;

// ATENÇÃO: esta view pode retornar múltiplas linhas por paciente
// (um paciente pode ter N responsáveis).
// O @Id aqui é paciente_id + nome — use consultas por paciente_id
// em vez de findById para evitar colisão.
public interface ResponsavelPaciente {
    String getNome();
    UUID getPacienteId();
}
