package br.org.apae.atendimento.dtos.request;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record AtendimentoRequestDTO(
        UUID profissionalId,
        UUID pacienteId,
        Map<String, Object> relatorio,
        LocalDateTime dataAtendimento
) {
}
