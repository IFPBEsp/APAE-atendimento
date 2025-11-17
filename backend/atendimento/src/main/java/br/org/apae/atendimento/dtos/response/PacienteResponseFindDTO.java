package br.org.apae.atendimento.dtos.response;

import java.time.LocalDate;

public record PacienteResponseFindDTO(
        Long id,
        String nomeCompleto,
        LocalDate dataDeNascimento,
        String contato,
        String cidade,
        String rua,
        String bairro,
        Integer numeroCasa,
        List<String> responsaveis,
        List<String> transtornos
) {}
