package br.org.apae.atendimento.dtos.request;

import br.org.apae.atendimento.entities.Topico;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public record AtendimentoRequestDTO(

        @NotNull(message = "O ID do paciente é obrigatório")
        UUID pacienteId,

        @NotEmpty(message = "O relatório deve conter pelo menos um tópico")
        @Valid
        Set<Topico> relatorio,

        @NotNull(message = "A data do atendimento é obrigatória")
        LocalDate data,

        @NotNull(message = "A hora do atendimento é obrigatória")
        LocalTime hora) {
}