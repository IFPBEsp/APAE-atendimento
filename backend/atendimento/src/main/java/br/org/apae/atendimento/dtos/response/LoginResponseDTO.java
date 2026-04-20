package br.org.apae.atendimento.dtos.response;

public record LoginResponseDTO(
        boolean success,
        String message
) {
}