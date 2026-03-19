package br.org.apae.atendimento.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record MagicLinkRequestDTO(@NotBlank(message = "O e-mail é obrigatório.")
                                  @Email(message = "Formato de e-mail inválido.")
                                  String email) {
}
