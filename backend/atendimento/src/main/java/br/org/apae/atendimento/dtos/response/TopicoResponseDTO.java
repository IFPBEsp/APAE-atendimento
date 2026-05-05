package br.org.apae.atendimento.dtos.response;

import java.util.UUID;

public record TopicoResponseDTO(
        UUID id,
        String titulo,
        String descricao
) {
}
