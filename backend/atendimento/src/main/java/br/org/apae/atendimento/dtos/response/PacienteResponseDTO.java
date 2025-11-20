package br.org.apae.atendimento.dtos.response;
import java.util.List;
import java.time.LocalDate;

public record PacienteResponseDTO(
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
