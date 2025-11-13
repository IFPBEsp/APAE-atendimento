package br.org.apae.atendimento.dtos.response.find;

public record ProfissionalResponseFindDTO(
        Long id,
        String primeiroNome,
        String nomeCompleto,
        String email,
        String contato,
        String crm
){};