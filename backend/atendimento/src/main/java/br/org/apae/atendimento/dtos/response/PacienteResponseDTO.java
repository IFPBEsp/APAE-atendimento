package br.org.apae.atendimento.dtos.response;
import java.util.List;
import java.util.UUID;
import java.time.LocalDate;

public record PacienteResponseDTO(
        UUID id,
        String nomeCompleto,
        LocalDate dataDeNascimento,
        String endereco,
        String contato,
        List<String> responsaveis,
        List<String> transtornos,
        String cpf,
        String fotoPreAssinada
) {}
