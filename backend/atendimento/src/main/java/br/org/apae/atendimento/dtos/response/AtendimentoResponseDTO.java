package br.org.apae.atendimento.dtos.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record AtendimentoResponseDTO(
    UUID id,
    List<TopicoResponseDTO> relatorio,
    LocalDate data,
    LocalTime hora,
    Long numeracao,
    UUID pacienteId,       // ← adicionado
    UUID profissionalId
) {
}
