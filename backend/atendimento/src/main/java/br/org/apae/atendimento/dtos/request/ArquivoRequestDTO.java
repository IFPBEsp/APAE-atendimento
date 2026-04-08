package br.org.apae.atendimento.dtos.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record ArquivoRequestDTO(

        @NotNull(message = "A data do arquivo é obrigatória")
        LocalDate data,

        @NotNull(message = "O tipo do arquivo é obrigatório")
        Long tipoArquivo,

        @NotNull(message = "O ID do paciente é obrigatório")
        UUID pacienteId,

        @NotBlank(message = "O título do arquivo é obrigatório")
        String titulo,

        String descricao
        ){
}
