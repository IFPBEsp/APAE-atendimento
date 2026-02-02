package br.org.apae.atendimento.dtos.request;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.UUID;

public record ArquivoRequestDTO(
        LocalDate data,
        Long tipoArquivo,
        UUID pacienteId,
        String titulo,
        String descricao
        ){
}
