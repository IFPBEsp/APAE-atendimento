package br.org.apae.atendimento.dtos.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public record AgendamentoRequestDTO(
        UUID profissionalId,
        UUID pacienteId,
        LocalDate data,
        LocalTime hora) {
}
