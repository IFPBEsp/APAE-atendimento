package br.org.apae.atendimento.dtos.response;

import java.time.LocalDate;
import java.util.UUID;

public record ArquivoResponseDTO(
        String objectName,
        String presignedUrl,
        LocalDate data,
        String nomeArquivo,
        String titulo,
        String descricao
    )
{
}
