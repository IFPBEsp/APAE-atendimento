package br.org.apae.atendimento.dtos;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record AtendimentoRequestDTO(
        Long profissionalId,
        UUID pacienteId,
        Map<String, Object> relatorio,
        LocalDateTime dataAtendimento
) {
}
