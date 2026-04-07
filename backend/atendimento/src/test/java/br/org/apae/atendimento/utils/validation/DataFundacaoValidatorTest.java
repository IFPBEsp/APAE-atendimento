package br.org.apae.atendimento.utils.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DataFundacaoValidatorTest {

    private DataFundacaoValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new DataFundacaoValidator();
    }

    @Test
    @DisplayName("Deve validar data atual (Limite Superior)")
    void deveValidarDataAtual() {
        assertTrue(validator.isValid(LocalDate.now(), context));
    }

    @Test
    @DisplayName("Deve invalidar data futura")
    void deveInvalidarDataFutura() {
        assertFalse(validator.isValid(LocalDate.now().plusDays(1), context));
    }

    @Test
    @DisplayName("Deve invalidar data anterior à fundação (Ex: 1980)")
    void deveInvalidarDataMuitoAntiga() {
        assertFalse(validator.isValid(LocalDate.of(1980, 1, 1), context));
    }

    @Test
    @DisplayName("Deve invalidar data anterior à fundação (21/09/1993)")
    void deveInvalidarDataAnteriorFundacao() {
        // 20/09/1993
        assertFalse(validator.isValid(LocalDate.of(1993, 9, 20), context));
    }

    @Test
    @DisplayName("Deve validar data da fundação (21/09/1993)")
    void deveValidarDataFundacao() {
        assertTrue(validator.isValid(LocalDate.of(1993, 9, 21), context));
    }

    @Test
    @DisplayName("Deve retornar true para valor nulo (deve ser tratado por @NotNull)")
    void deveRetornarTrueParaNull() {
        assertTrue(validator.isValid(null, context));
    }
}
