package br.org.apae.atendimento.dtos;

import br.org.apae.atendimento.entities.Arquivo;

import java.time.LocalDate;

public record ArquivoDTO(
        String objectName,
        String presignedUrl,
        String nomeAnexo,
        LocalDate data,
        Long tipoArquivoId
        ){
    public static ArquivoDTO toDTO(Arquivo arquivo){
        return new ArquivoDTO(
                arquivo.getObjectName(),
                arquivo.getPresignedUrl(),
                arquivo.getNomeArquivo(),
                arquivo.getData(),
                arquivo.getTipo().getId()
        );
    }
}
