package br.org.apae.atendimento.exceptions;

import java.time.LocalDateTime;

public record ErrorResponse(LocalDateTime timeStamp, Integer status, String message) {
}
