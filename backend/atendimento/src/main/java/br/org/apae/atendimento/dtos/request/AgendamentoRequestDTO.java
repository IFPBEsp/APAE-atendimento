package br.org.apae.atendimento.dtos.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record AgendamentoRequestDTO(

        @NotNull(message = "O ID do paciente é obrigatório")
        UUID pacienteId,

        @NotNull(message = "A data do agendamento é obrigatória")
        @FutureOrPresent(message = "A data do agendamento não pode estar no passado")
        LocalDate data,

        @NotNull(message = "A hora do agendamento é obrigatória")
        LocalTime hora) {
}
