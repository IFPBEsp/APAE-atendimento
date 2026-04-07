package br.org.apae.atendimento.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class StringHandlerTest {

    @Test
    @DisplayName("Deve normalizar string removendo espaços extras e trim")
    void deveNormalizarString() {
        String input = "  RELATÓRIO    MENSAL  ";
        String expected = "RELATÓRIO MENSAL";
        assertEquals(expected, StringHandler.normalizar(input));
    }

    @Test
    @DisplayName("Deve retornar null ao normalizar string nula")
    void deveRetornarNullAoNormalizarNull() {
        assertNull(StringHandler.normalizar(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "<script>alert('xss')</script>Título",
        "Título <img src=x onerror=alert(1)>",
        "<b>Título</b> com <i>HTML</i>"
    })
    @DisplayName("Deve sanitizar string removendo tags HTML perigosas")
    void deveSanitizarString(String input) {
        String result = StringHandler.sanitizar(input);
        assertFalse(result.contains("<script>"));
        assertFalse(result.contains("<img>"));
        assertFalse(result.contains("<b>"));
        assertFalse(result.contains("<i>"));
    }

    @Test
    @DisplayName("Deve retornar null ao sanitizar string nula")
    void deveRetornarNullAoSanitizarNull() {
        assertNull(StringHandler.sanitizar(null));
    }

    @Test
    @DisplayName("Deve canonicalizar string para lowercase")
    void deveCanonicalizarParaLowercase() {
        String input = "Relatório de Atendimento";
        String expected = "relatório de atendimento";
        assertEquals(expected, StringHandler.canonicalizar(input));
    }

    @Test
    @DisplayName("Deve retornar null ao canonicalizar string nula")
    void deveRetornarNullAoCanonicalizarNull() {
        assertNull(StringHandler.canonicalizar(null));
    }
}
