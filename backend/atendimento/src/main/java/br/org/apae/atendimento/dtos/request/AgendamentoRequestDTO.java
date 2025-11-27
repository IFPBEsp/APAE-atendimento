package br.org.apae.atendimento.dtos.request;

import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public record AgendamentoRequestDTO(UUID pacientId,
                                    UUID profissionalId,
                                    LocalDate data,
                                    LocalTime hora,
                                    Long numeroAtendimento) {
}
