package br.org.apae.atendimento.dtos.response;

import java.time.YearMonth;
import java.util.List;

public record MesAnoAtendimentoResponseDTO(
        YearMonth mesAno,
        List<AtendimentoResponseDTO> atendimentos
) {
}
