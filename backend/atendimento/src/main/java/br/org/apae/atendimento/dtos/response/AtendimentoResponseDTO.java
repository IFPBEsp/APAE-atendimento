package br.org.apae.atendimento.dtos.response;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.UUID;

public record AtendimentoResponseDTO(
    UUID id,
    Map<String, Object> relatorio,
    LocalDate data,
    LocalTime hora,
    Long numeracao
) {
}
