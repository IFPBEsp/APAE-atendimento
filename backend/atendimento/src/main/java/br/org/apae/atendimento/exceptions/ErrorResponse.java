package br.org.apae.atendimento.exceptions;

import java.time.LocalDateTime;

public record ErrorResponse(LocalDateTime timestamp, Integer status, String message) {
}
