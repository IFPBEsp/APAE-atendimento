package br.org.apae.atendimento.dtos.response;
import java.time.LocalDateTime;
import java.util.Map;

public record ConsultaResponseCreateOrFindDTO(
    Long id,
    Map<String, Object> relatorio,
    LocalDateTime dataConsulta,
    boolean status
) {
}
