package br.org.apae.atendimento.dtos.response;
import br.org.apae.atendimento.entities.Topico;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

public record AtendimentoResponseDTO(
    UUID id,
    Set<Topico> relatorio,
    LocalDate data,
    LocalTime hora,
    Long numeracao
) {
}
