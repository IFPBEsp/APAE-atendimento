package br.org.apae.atendimento.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class JwtAuthenticationFilterTest {

    private final JwtAuthenticationFilter filter =
            new JwtAuthenticationFilter(mock(JwtService.class), mock(TokenBlocklistService.class));

    private MockHttpServletRequest request(String method, String contextPath, String path) {
        MockHttpServletRequest request = new MockHttpServletRequest(method, contextPath + path);
        request.setContextPath(contextPath);
        return request;
    }

    @Test
    @DisplayName("Deve ignorar o filtro nas rotas públicas de auth considerando o context path")
    void deveIgnorarRotasPublicasComContextPath() {
        assertTrue(filter.shouldNotFilter(request("POST", "/atendimento", "/auth/login")));
        assertTrue(filter.shouldNotFilter(request("POST", "/atendimento", "/auth/logout")));
        assertTrue(filter.shouldNotFilter(request("POST", "/atendimento", "/auth/esqueci-senha")));
    }

    @Test
    @DisplayName("Deve ignorar o filtro nas rotas públicas de auth sem context path")
    void deveIgnorarRotasPublicasSemContextPath() {
        assertTrue(filter.shouldNotFilter(request("POST", "", "/auth/login")));
        assertTrue(filter.shouldNotFilter(request("POST", "", "/auth/logout")));
        assertTrue(filter.shouldNotFilter(request("POST", "", "/auth/esqueci-senha")));
    }

    @Test
    @DisplayName("Deve ignorar o filtro em requisições OPTIONS e no health check")
    void deveIgnorarOptionsEHealth() {
        assertTrue(filter.shouldNotFilter(request("OPTIONS", "/atendimento", "/agendamento")));
        assertTrue(filter.shouldNotFilter(request("GET", "/atendimento", "/actuator/health")));
    }

    @Test
    @DisplayName("Deve continuar filtrando rotas protegidas")
    void deveFiltrarRotasProtegidas() {
        assertFalse(filter.shouldNotFilter(request("GET", "/atendimento", "/auth/me")));
        assertFalse(filter.shouldNotFilter(request("POST", "/atendimento", "/auth/redefinir-senha")));
        assertFalse(filter.shouldNotFilter(request("GET", "/atendimento", "/pacientes/search")));
    }
}
