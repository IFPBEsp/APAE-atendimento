package br.org.apae.atendimento.utils.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class DataFundacaoValidator implements ConstraintValidator<ValidDataFundacao, LocalDate> {

    // APAE Esperança foi fundada em 21 de setembro de 1993
    private static final LocalDate DATA_FUNDACAO_APAE = LocalDate.of(1993, 9, 21);

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        LocalDate hoje = LocalDate.now();
        LocalDate limite30AnosAtras = hoje.minusYears(30);

        // A data deve estar entre (Hoje - 30 anos)
        boolean dentroDoRange30Anos = !value.isBefore(limite30AnosAtras) && !value.isAfter(hoje);
        boolean aposFundacao = !value.isBefore(DATA_FUNDACAO_APAE);

        return dentroDoRange30Anos && aposFundacao;
    }
}
