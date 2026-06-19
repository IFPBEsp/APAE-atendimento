package br.org.apae.atendimento.dtos.response;

public record AutenticacaoResponseDTO(
        String token,
        Boolean primerioAcesso,
        String redirectTo
) {
}
