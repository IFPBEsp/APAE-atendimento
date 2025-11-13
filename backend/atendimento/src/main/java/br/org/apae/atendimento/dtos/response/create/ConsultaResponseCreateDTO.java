package br.org.apae.atendimento.dtos.response.create;
import java.time.LocalDateTime;
import java.util.Map;

public record ConsultaResponseCreateDTO(
    Long id,
    Map<String, String> relatorio,
    LocalDateTime dataConsulta,
    boolean status
) {
}
