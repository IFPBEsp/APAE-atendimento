package br.org.apae.atendimento.dtos.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record AgendamentoResponseDTO(
        UUID id,
        UUID pacienteId,
        String nomePaciente,
        LocalDate data,
        LocalTime hora,
        String numeracao,
        boolean status,
        boolean externo
) {
}