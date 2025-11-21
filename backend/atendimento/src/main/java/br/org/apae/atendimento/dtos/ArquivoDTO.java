package br.org.apae.atendimento.dtos;
import java.time.LocalDate;

public record ArquivoDTO(
        String objectName,
        String bucket,
        String presignedUrl,
        String nomeAnexo,
        LocalDate data
) {
}
