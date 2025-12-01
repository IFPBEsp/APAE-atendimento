package br.org.apae.atendimento.dtos.response;
import java.util.List;
import java.util.UUID;
import java.time.LocalDate;

public record PacienteResponseDTO(
        UUID id,
        String nomeCompleto,
        LocalDate dataDeNascimento,
        String contato,
        String rua,
        String bairro,
        Integer numeroCasa,
        List<String> responsaveis,
        List<String> transtornos,
        String cidade,
        String cpf
) {}
