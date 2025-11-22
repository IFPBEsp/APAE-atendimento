package br.org.apae.atendimento.dtos;
import java.time.LocalDate;
import java.util.UUID;

public record ArquivoDTO(
        String objectName,
        String bucket,
        String presignedUrl,
        String nomeAnexo,
        LocalDate data,
        UUID pacienteId,
        Long profissionalId
) {
}
