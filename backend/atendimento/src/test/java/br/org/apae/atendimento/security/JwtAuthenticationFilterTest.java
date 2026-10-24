package br.org.apae.atendimento.security;

import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterTest {

    private static final String TOKEN = "token-valido";
    private static final String JTI = "jti-123";

    private JwtService jwtService;
    private TokenBlocklistService tokenBlocklistService;
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        tokenBlocklistService = mock(TokenBlocklistService.class);
        filter = new JwtAuthenticationFilter(jwtService, tokenBlocklistService);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private MockHttpServletRequest request(
            String method, String contextPath, String path) {
        MockHttpServletRequest request =
                new MockHttpServletRequest(method, contextPath + path);
        request.setContextPath(contextPath);
        return request;
    }

    private MockHttpServletRequest protectedRequest() {
        return request("GET", "", "/pacientes");
    }

    private MockHttpServletResponse response() {
        return new MockHttpServletResponse();
    }

    private void configurarTokenValido(UUID userId, List<String> roles) {
        when(jwtService.tokenValido(TOKEN)).thenReturn(true);
        when(jwtService.extrairJti(TOKEN)).thenReturn(JTI);
        when(tokenBlocklistService.estaRevogado(JTI)).thenReturn(false);
        when(jwtService.extrairSubject(TOKEN)).thenReturn(userId.toString());
        when(jwtService.extrairRoles(TOKEN)).thenReturn(roles);
    }

    @Test
    @DisplayName("Deve ignorar rotas públicas de autenticação com context path")
    void deveIgnorarRotasPublicasComContextPath() {
        assertTrue(filter.shouldNotFilter(
                request("POST", "/atendimento", "/auth/login")));
        assertTrue(filter.shouldNotFilter(
                request("POST", "/atendimento", "/auth/logout")));
        assertTrue(filter.shouldNotFilter(
                request("POST", "/atendimento", "/auth/esqueci-senha")));
    }

    @Test
    @DisplayName("Deve ignorar rotas públicas sem context path")
    void deveIgnorarRotasPublicasSemContextPath() {
        assertTrue(filter.shouldNotFilter(
                request("POST", "", "/auth/login")));
        assertTrue(filter.shouldNotFilter(
                request("POST", "", "/auth/logout")));
        assertTrue(filter.shouldNotFilter(
                request("POST", "", "/auth/esqueci-senha")));
    }

    @Test
    @DisplayName("Deve ignorar OPTIONS, raiz, erro e health check")
    void deveIgnorarOptionsRaizErroEHealth() {
        assertTrue(filter.shouldNotFilter(
                request("OPTIONS", "/atendimento", "/pacientes")));
        assertTrue(filter.shouldNotFilter(request("GET", "", "/")));
        assertTrue(filter.shouldNotFilter(request("GET", "", "/error")));
        assertTrue(filter.shouldNotFilter(
                request("GET", "", "/actuator/health")));
        assertTrue(filter.shouldNotFilter(
                request("GET", "", "/actuator/health/readiness")));
    }

    @Test
    @DisplayName("Deve continuar filtrando rotas protegidas")
    void deveFiltrarRotasProtegidas() {
        assertFalse(filter.shouldNotFilter(
                request("GET", "/atendimento", "/auth/me")));
        assertFalse(filter.shouldNotFilter(
                request("POST", "/atendimento", "/auth/redefinir-senha")));
        assertFalse(filter.shouldNotFilter(
                request("GET", "/atendimento", "/pacientes/search")));
    }

    @Test
    @DisplayName("Deve continuar a cadeia quando não houver cookies")
    void deveContinuarQuandoNaoHouverCookies()
            throws ServletException, IOException {
        MockHttpServletRequest request = protectedRequest();
        MockHttpServletResponse response = response();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertSame(request, chain.getRequest());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verifyNoInteractions(jwtService, tokenBlocklistService);
    }

    @Test
    @DisplayName("Deve continuar a cadeia quando o cookie token estiver vazio")
    void deveContinuarQuandoCookieTokenEstiverVazio()
            throws ServletException, IOException {
        MockHttpServletRequest request = protectedRequest();
        request.setCookies(new jakarta.servlet.http.Cookie("token", "   "));
        MockHttpServletResponse response = response();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertSame(request, chain.getRequest());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verifyNoInteractions(jwtService, tokenBlocklistService);
    }

    @Test
    @DisplayName("Deve retornar 401 quando o token for inválido")
    void deveRetornar401QuandoTokenForInvalido()
            throws ServletException, IOException {
        MockHttpServletRequest request = protectedRequest();
        request.setCookies(new jakarta.servlet.http.Cookie("token", TOKEN));
        MockHttpServletResponse response = response();
        MockFilterChain chain = new MockFilterChain();

        when(jwtService.tokenValido(TOKEN)).thenReturn(false);

        filter.doFilter(request, response, chain);

        assertEquals(401, response.getStatus());
        assertEquals("Token inválido", response.getErrorMessage());
        assertNull(chain.getRequest());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtService, never()).extrairJti(TOKEN);
    }

    @Test
    @DisplayName("Deve retornar 401 quando o JTI for nulo")
    void deveRetornar401QuandoJtiForNulo()
            throws ServletException, IOException {
        MockHttpServletRequest request = protectedRequest();
        request.setCookies(new jakarta.servlet.http.Cookie("token", TOKEN));
        MockHttpServletResponse response = response();

        when(jwtService.tokenValido(TOKEN)).thenReturn(true);
        when(jwtService.extrairJti(TOKEN)).thenReturn(null);

        filter.doFilter(request, response, new MockFilterChain());

        assertEquals(401, response.getStatus());
        assertEquals("Token inválido", response.getErrorMessage());
        verify(tokenBlocklistService, never()).estaRevogado(null);
    }

    @Test
    @DisplayName("Deve retornar 401 quando o JTI estiver em branco")
    void deveRetornar401QuandoJtiEstiverEmBranco()
            throws ServletException, IOException {
        MockHttpServletRequest request = protectedRequest();
        request.setCookies(new jakarta.servlet.http.Cookie("token", TOKEN));
        MockHttpServletResponse response = response();

        when(jwtService.tokenValido(TOKEN)).thenReturn(true);
        when(jwtService.extrairJti(TOKEN)).thenReturn(" ");

        filter.doFilter(request, response, new MockFilterChain());

        assertEquals(401, response.getStatus());
        verify(tokenBlocklistService, never()).estaRevogado(" ");
    }

    @Test
    @DisplayName("Deve retornar 401 quando o token estiver revogado")
    void deveRetornar401QuandoTokenEstiverRevogado()
            throws ServletException, IOException {
        MockHttpServletRequest request = protectedRequest();
        request.setCookies(new jakarta.servlet.http.Cookie("token", TOKEN));
        MockHttpServletResponse response = response();

        when(jwtService.tokenValido(TOKEN)).thenReturn(true);
        when(jwtService.extrairJti(TOKEN)).thenReturn(JTI);
        when(tokenBlocklistService.estaRevogado(JTI)).thenReturn(true);

        filter.doFilter(request, response, new MockFilterChain());

        assertEquals(401, response.getStatus());
        assertEquals("Token inválido", response.getErrorMessage());
        verify(jwtService, never()).extrairSubject(TOKEN);
    }

    @Test
    @DisplayName("Deve retornar 401 quando o subject não for um UUID válido")
    void deveRetornar401QuandoSubjectNaoForUuidValido()
            throws ServletException, IOException {
        MockHttpServletRequest request = protectedRequest();
        request.setCookies(new jakarta.servlet.http.Cookie("token", TOKEN));
        MockHttpServletResponse response = response();

        when(jwtService.tokenValido(TOKEN)).thenReturn(true);
        when(jwtService.extrairJti(TOKEN)).thenReturn(JTI);
        when(tokenBlocklistService.estaRevogado(JTI)).thenReturn(false);
        when(jwtService.extrairSubject(TOKEN)).thenReturn("uuid-invalido");

        filter.doFilter(request, response, new MockFilterChain());

        assertEquals(401, response.getStatus());
        assertEquals("Token inválido", response.getErrorMessage());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Deve retornar 401 quando ocorrer exceção JWT")
    void deveRetornar401QuandoOcorrerExcecaoJwt()
            throws ServletException, IOException {
        MockHttpServletRequest request = protectedRequest();
        request.setCookies(new jakarta.servlet.http.Cookie("token", TOKEN));
        MockHttpServletResponse response = response();

        when(jwtService.tokenValido(TOKEN))
                .thenThrow(new MalformedJwtException("Token malformado"));

        filter.doFilter(request, response, new MockFilterChain());

        assertEquals(401, response.getStatus());
        assertEquals("Token inválido", response.getErrorMessage());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Deve autenticar usuário com UUID e roles do token")
    void deveAutenticarUsuarioComUuidERoles()
            throws ServletException, IOException {
        UUID userId = UUID.randomUUID();
        List<String> roles = List.of("ROLE_ADMIN", "ROLE_PROFISSIONAL");

        configurarTokenValido(userId, roles);

        MockHttpServletRequest request = protectedRequest();
        request.setCookies(new jakarta.servlet.http.Cookie("token", TOKEN));
        MockHttpServletResponse response = response();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertSame(request, chain.getRequest());
        assertEquals(200, response.getStatus());

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        assertInstanceOf(UsernamePasswordAuthenticationToken.class, authentication);
        assertTrue(authentication.isAuthenticated());

        UsuarioAutenticado principal =
                assertInstanceOf(UsuarioAutenticado.class,
                        authentication.getPrincipal());

        assertEquals(userId, principal.getId());
        assertEquals(
                List.of(
                        new SimpleGrantedAuthority("ROLE_ADMIN"),
                        new SimpleGrantedAuthority("ROLE_PROFISSIONAL")),
                authentication.getAuthorities());

        verify(tokenBlocklistService).estaRevogado(JTI);
    }

    @Test
    @DisplayName("Deve usar ROLE_PROFISSIONAL quando as roles forem nulas")
    void deveUsarRolePadraoQuandoRolesForemNulas()
            throws ServletException, IOException {
        UUID userId = UUID.randomUUID();
        configurarTokenValido(userId, null);

        MockHttpServletRequest request = protectedRequest();
        request.setCookies(new jakarta.servlet.http.Cookie("token", TOKEN));
        MockHttpServletResponse response = response();

        filter.doFilter(request, response, new MockFilterChain());

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        assertInstanceOf(UsernamePasswordAuthenticationToken.class, authentication);
        assertEquals(
                List.of(new SimpleGrantedAuthority("ROLE_PROFISSIONAL")),
                authentication.getAuthorities());
    }

    @Test
    @DisplayName("Deve usar ROLE_PROFISSIONAL quando as roles estiverem vazias")
    void deveUsarRolePadraoQuandoRolesEstiveremVazias()
            throws ServletException, IOException {
        UUID userId = UUID.randomUUID();
        configurarTokenValido(userId, List.of());

        MockHttpServletRequest request = protectedRequest();
        request.setCookies(new jakarta.servlet.http.Cookie("token", TOKEN));
        MockHttpServletResponse response = response();

        filter.doFilter(request, response, new MockFilterChain());

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        assertInstanceOf(UsernamePasswordAuthenticationToken.class, authentication);
        assertEquals(
                List.of(new SimpleGrantedAuthority("ROLE_PROFISSIONAL")),
                authentication.getAuthorities());
    }

    @Test
    @DisplayName("Deve preservar autenticação que já existe")
    void devePreservarAutenticacaoExistente()
            throws ServletException, IOException {
        Authentication existente =
                new UsernamePasswordAuthenticationToken("usuario", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(existente);

        MockHttpServletRequest request = protectedRequest();
        request.setCookies(new jakarta.servlet.http.Cookie("token", TOKEN));
        MockHttpServletResponse response = response();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertSame(request, chain.getRequest());
        assertSame(existente,
                SecurityContextHolder.getContext().getAuthentication());
        verifyNoInteractions(jwtService, tokenBlocklistService);
    }

    @Test
    @DisplayName("Deve manter a autenticação no contexto durante a cadeia de filtros")
    void deveManterAutenticacaoDuranteCadeiaDeFiltros()
            throws ServletException, IOException {
        UUID userId = UUID.randomUUID();
        configurarTokenValido(userId, List.of("ROLE_PROFISSIONAL"));

        MockHttpServletRequest request = protectedRequest();
        request.setCookies(new jakarta.servlet.http.Cookie("token", TOKEN));
        MockHttpServletResponse response = response();

        FilterChain chain = (req, res) -> assertTrue(
                SecurityContextHolder.getContext().getAuthentication() != null);

        filter.doFilter(request, response, chain);

        assertEquals(200, response.getStatus());
        assertInstanceOf(UsuarioAutenticado.class,
                SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal());
    }
}
