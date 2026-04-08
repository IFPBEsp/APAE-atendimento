package br.org.apae.atendimento.utils.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.ZoneId;

public class DataFundacaoValidator implements ConstraintValidator<ValidDataFundacao, LocalDate> {

    // APAE Esperança foi fundada em 21 de setembro de 1993
    private static final LocalDate DATA_FUNDACAO_APAE = LocalDate.of(1993, 9, 21);

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        LocalDate hoje = LocalDate.now(ZoneId.of("America/Fortaleza"));

        // A data deve estar entre a fundação da APAE e a data atual
        boolean aposFundacao = !value.isBefore(DATA_FUNDACAO_APAE);
        boolean naoNoFuturo = !value.isAfter(hoje);

        return aposFundacao && naoNoFuturo;
    }
}
