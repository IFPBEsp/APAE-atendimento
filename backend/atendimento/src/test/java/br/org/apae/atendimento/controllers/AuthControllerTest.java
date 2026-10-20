package br.org.apae.atendimento.controllers;

import br.org.apae.atendimento.dtos.request.LoginRequestDTO;
import br.org.apae.atendimento.dtos.request.RedefinirSenhaRequestDTO;
import br.org.apae.atendimento.dtos.response.AutenticacaoResponseDTO;
import br.org.apae.atendimento.security.JwtService;
import br.org.apae.atendimento.security.TokenBlocklistService;
import br.org.apae.atendimento.security.UsuarioAutenticado;
import br.org.apae.atendimento.services.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private TokenBlocklistService tokenBlocklistService;

    @Test
    @DisplayName("Deve realizar login e configurar cookie de autenticação")
    void deveRealizarLoginComSucesso() throws Exception {
        when(authService.autenticar(any(LoginRequestDTO.class)))
                .thenReturn(new AutenticacaoResponseDTO("jwt-teste", false, "/inicio"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "usuario@apae.org.br",
                                  "password": "senha123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login realizado com sucesso"))
                .andExpect(jsonPath("$.primeiroAcesso").value(false))
                .andExpect(jsonPath("$.redirectTo").value("/inicio"))
                .andExpect(header().string(HttpHeaders.SET_COOKIE,
                        org.hamcrest.Matchers.allOf(
                                org.hamcrest.Matchers.containsString("token=jwt-teste"),
                                org.hamcrest.Matchers.containsString("HttpOnly"),
                                org.hamcrest.Matchers.containsString("Path=/"),
                                org.hamcrest.Matchers.containsString("SameSite=Strict")
                        )));

        verify(authService).autenticar(any(LoginRequestDTO.class));
    }

    @Test
    @DisplayName("Deve rejeitar login com e-mail inválido")
    void deveRejeitarLoginComEmailInvalido() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "email-invalido",
                                  "password": "senha123"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    @DisplayName("Deve rejeitar login com senha menor que oito caracteres")
    void deveRejeitarLoginComSenhaCurta() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "usuario@apae.org.br",
                                  "password": "123"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

@Test
@DisplayName("Deve redefinir senha do usuário autenticado")
void deveRedefinirSenhaComSucesso() throws Exception {
    UUID usuarioId = UUID.fromString("44444444-4444-4444-4444-444444444444");
    UsuarioAutenticado usuario = new UsuarioAutenticado(usuarioId);
    Authentication authentication = mock(Authentication.class);

    when(authentication.getPrincipal()).thenReturn(usuario);
    when(authentication.isAuthenticated()).thenReturn(true);

    SecurityContextHolder.getContext().setAuthentication(authentication);

    try {
        mockMvc.perform(post("/auth/redefinir-senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "novaSenha": "novaSenha123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Senha redefinida com sucesso"));

        verify(authService).redefinirSenha(
                eq(usuarioId),
                any(RedefinirSenhaRequestDTO.class)
        );
    } finally {
        SecurityContextHolder.clearContext();
    }
}
@Test
@DisplayName("Deve rejeitar redefinição com senha curta")
void deveRejeitarRedefinicaoComSenhaCurta() throws Exception {
    UUID usuarioId = UUID.fromString("44444444-4444-4444-4444-444444444444");
    UsuarioAutenticado usuario = new UsuarioAutenticado(usuarioId);
    Authentication authentication = mock(Authentication.class);

    when(authentication.getPrincipal()).thenReturn(usuario);
    when(authentication.isAuthenticated()).thenReturn(true);

    SecurityContextHolder.getContext().setAuthentication(authentication);

    try {
        mockMvc.perform(post("/auth/redefinir-senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "novaSenha": "123"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    } finally {
        SecurityContextHolder.clearContext();
    }
}


    @Test
    @DisplayName("Deve revogar token e limpar cookie ao realizar logout")
    void deveRealizarLogoutComTokenValido() throws Exception {
        when(jwtService.tokenValido("jwt-teste")).thenReturn(true);
        when(jwtService.extrairJti("jwt-teste")).thenReturn("jti-teste");
        when(jwtService.extrairExpiracao("jwt-teste")).thenReturn(new Date());

        mockMvc.perform(post("/auth/logout")
                        .cookie(new jakarta.servlet.http.Cookie("token", "jwt-teste")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Logout realizado com sucesso"))
                .andExpect(header().string(HttpHeaders.SET_COOKIE,
                        org.hamcrest.Matchers.allOf(
                                org.hamcrest.Matchers.containsString("token="),
                                org.hamcrest.Matchers.containsString("Max-Age=0"),
                                org.hamcrest.Matchers.containsString("HttpOnly"),
                                org.hamcrest.Matchers.containsString("SameSite=Strict")
                        )));

        verify(tokenBlocklistService).revogar(eq("jti-teste"), any(Date.class));
    }

    @Test
    @DisplayName("Deve limpar cookie no logout mesmo sem token")
    void deveRealizarLogoutSemToken() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(header().string(HttpHeaders.SET_COOKIE,
                        org.hamcrest.Matchers.allOf(
                                org.hamcrest.Matchers.containsString("token="),
                                org.hamcrest.Matchers.containsString("Max-Age=0")
                        )));

        verifyNoInteractions(jwtService);
        verifyNoInteractions(tokenBlocklistService);
    }

    @Test
    @DisplayName("Deve retornar sucesso quando usuário está autenticado")
    void deveRetornarMeAutenticado() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);

        mockMvc.perform(get("/auth/me")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Autenticado"));
    }

    @Test
    @DisplayName("Deve retornar não autenticado quando não existe autenticação")
    void deveRetornarMeNaoAutenticado() throws Exception {
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Não autenticado"));
    }
}