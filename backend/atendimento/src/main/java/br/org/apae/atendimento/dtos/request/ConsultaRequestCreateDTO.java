package br.org.apae.atendimento.dtos.request;

import java.time.LocalDateTime;
import java.util.Map;

public record ConsultaRequestCreateDTO(
        Long pacienteId,
        Long profissionalId,
        Map<String, Object> relatorio,
        LocalDateTime dataConsulta
) {
}
