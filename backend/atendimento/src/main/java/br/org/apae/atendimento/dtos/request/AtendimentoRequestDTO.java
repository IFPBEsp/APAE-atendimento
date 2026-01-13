package br.org.apae.atendimento.dtos.request;

import br.org.apae.atendimento.entities.Topico;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public record AtendimentoRequestDTO(
        UUID profissionalId,
        UUID pacienteId,
        Set<Topico> relatorio,
        LocalDate data,
        LocalTime hora
) {
}