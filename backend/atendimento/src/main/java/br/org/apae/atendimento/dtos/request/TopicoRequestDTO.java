package br.org.apae.atendimento.dtos.request;

import jakarta.validation.constraints.NotBlank;

public record TopicoRequestDTO(
        @NotBlank(message = "O título do tópico é obrigatório")
        String titulo,
        @NotBlank(message = "A descrição do tópico é obrigatória")
        String descricao
) {
}