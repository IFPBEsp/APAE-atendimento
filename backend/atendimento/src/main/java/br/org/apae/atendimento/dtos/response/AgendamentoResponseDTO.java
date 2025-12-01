package br.org.apae.atendimento.dtos.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record AgendamentoResponseDTO(
        UUID atendimentoId,
        UUID pacienteId,
        String nomePaciente,
        LocalDate data,
        LocalTime time,
        Long numeroAtendimento,
        boolean status) {
}
