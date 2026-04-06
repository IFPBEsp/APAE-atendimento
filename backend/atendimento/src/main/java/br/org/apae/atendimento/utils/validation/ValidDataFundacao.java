package br.org.apae.atendimento.utils.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DataFundacaoValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDataFundacao {
    String message() default "Data fora do período permitido (últimos 30 anos) ou anterior à fundação da APAE (21/09/1993).";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
