package br.org.apae.atendimento.dtos.request;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.UUID;

public record ArquivoRequestDTO(
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate data,
        Long tipoArquivo,
        UUID profissionalId,
        UUID pacienteId,
        String titulo,
        String descricao
        ){
}
