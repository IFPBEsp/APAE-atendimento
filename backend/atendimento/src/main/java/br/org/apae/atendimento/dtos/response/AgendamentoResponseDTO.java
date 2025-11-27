package br.org.apae.atendimento.dtos.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record AgendamentoResponseDTO(UUID pacienteId,
                                     LocalTime time,
                                     LocalDate data,
                                     Long numeroAtendimento) {
}
