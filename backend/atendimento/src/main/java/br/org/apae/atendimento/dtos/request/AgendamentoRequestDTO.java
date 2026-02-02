package br.org.apae.atendimento.dtos.request;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record AgendamentoRequestDTO(
        UUID pacienteId,
        LocalDate data,
        LocalTime hora) {
}
