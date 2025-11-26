package br.org.apae.atendimento.dtos.response;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record AtendimentoResponseDTO(
    UUID id,
    Map<String, Object> relatorio,
    LocalDateTime dataConsulta,
    boolean status
) {
}
