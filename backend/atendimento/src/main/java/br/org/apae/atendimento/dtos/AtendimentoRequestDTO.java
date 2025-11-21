package br.org.apae.atendimento.dtos;

import java.time.LocalDateTime;
import java.util.Map;

public record AtendimentoRequestDTO(
        Long profissionalId,
        Long pacienteId,
        Map<String, Object> relatorio,
        LocalDateTime dataAtendimento
) {
}
