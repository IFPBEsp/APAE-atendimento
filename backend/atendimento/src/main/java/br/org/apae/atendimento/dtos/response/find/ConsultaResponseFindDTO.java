package br.org.apae.atendimento.dtos.response.find;

import java.time.LocalDateTime;
import java.util.Map;

public record ConsultaResponseFindDTO(
        Long id,
        LocalDateTime dataConsulta,
        boolean status,
        Map<String, String> relatorio
){}

/// Faltando paciente, profissional?