package br.org.apae.atendimento.dtos.response;

import java.time.LocalDate;
import java.util.List;

public record DiaAgendamentoResponseDTO(
        LocalDate dia,
        List<AgendamentoResponseDTO> agendamentos
) {
}
