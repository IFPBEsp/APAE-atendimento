package br.org.apae.atendimento.dtos.response;

public record LoginResponseDTO(
        boolean success,
        String message,
        Boolean primeiroAcesso,
        String redirectTo
) {
    public LoginResponseDTO(boolean success, String message) {
        this(success, message, null, null);
    }
}