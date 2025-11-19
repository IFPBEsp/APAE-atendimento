package br.org.apae.atendimento.dtos.response;

public record ProfissionalResponseDTO(
        Long id,
        String primeiroNome,
        String nomeCompleto,
        String email,
        String contato,
        String crm
){};