package br.org.apae.atendimento.dtos.request;
import java.time.LocalDate;
import java.util.UUID;

public record ArquivoRequestDTO(
        LocalDate data,
        Long tipoArquivo,
        UUID profissionalId,
        UUID pacienteId,
        String titulo,
        String descricao
        ){
}
