package br.org.apae.atendimento.dtos.request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.UUID;

public record AtendimentoRequestDTO(
        UUID profissionalId,
        UUID pacienteId,
        Map<String, Object> relatorio,
        LocalDate data,
        LocalTime hora
) {
}