package br.org.apae.atendimento.dtos.response;

import java.util.UUID;

public record ProfissionalResponseDTO(
        UUID id,
        String primeiroNome,
        String nomeCompleto,
        String email,
        String contato
){};