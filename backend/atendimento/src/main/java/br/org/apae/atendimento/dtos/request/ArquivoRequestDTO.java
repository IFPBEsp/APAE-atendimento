package br.org.apae.atendimento.dtos.request;

import br.org.apae.atendimento.utils.validation.ValidDataFundacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.UUID;

public record ArquivoRequestDTO(

        @NotNull(message = "A data do arquivo é obrigatória")
        @ValidDataFundacao
        LocalDate data,

        @NotNull(message = "O tipo do arquivo é obrigatório")
        Long tipoArquivo,

        @NotNull(message = "O ID do paciente é obrigatório")
        UUID pacienteId,

        @NotBlank(message = "O título do arquivo é obrigatório")
        @Pattern(
                regexp = "^(?=.*[\\p{L}\\p{M}])[\\p{L}\\p{M}0-9 \\-:/()']*$",
                message = "Título inválido. Use letras, números e pontuações permitidas. Não pode ser composto apenas por números ou espaços."
        )
        String titulo,

        @NotBlank(message = "A descrição do arquivo é obrigatória")
        @Pattern(
                regexp = "^(?=.*[\\p{L}\\p{M}])[\\p{L}\\p{M}0-9 \\-:/()'%&#]*$",
                message = "Descrição inválida. Use letras, números e pontuações permitidas. Não pode ser composta apenas por números ou espaços."
        )
        String descricao
) {}