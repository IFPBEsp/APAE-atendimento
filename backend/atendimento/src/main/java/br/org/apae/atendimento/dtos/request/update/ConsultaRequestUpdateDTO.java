package br.org.apae.atendimento.dtos.request.update;

import java.time.LocalDateTime;

public record ConsultaRequestUpdateDTO(
        Long id,
        LocalDateTime novaDataConsulta
) {
}
