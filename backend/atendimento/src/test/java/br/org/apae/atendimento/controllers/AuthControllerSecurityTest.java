package br.org.apae.atendimento.controllers;

import br.org.apae.atendimento.dtos.request.RedefinirSenhaRequestDTO;
import br.org.apae.atendimento.security.JwtService;
import br.org.apae.atendimento.security.SecurityConfig;
import br.org.apae.atendimento.security.TokenBlocklistService;
import br.org.apae.atendimento.security.UsuarioAutenticado;
import br.org.apae.atendimento.services.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes do AuthController com a cadeia de filtros do Spring Security ligada
 * (SecurityConfig real). O MockAuthenticationFilter é desativado para que
 * requisições sem autenticação realmente cheguem sem usuário.
 */
@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = "auth.mock.enabled=false")
class AuthControllerSecurityTest {

    private static final String REDEFINIR_SENHA_URL = "/auth/redefinir-senha";
    private static final String ME_URL = "/auth/me";
    private static final UUID USUARIO_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private TokenBlocklistService tokenBlocklistService;

    private static UsernamePasswordAuthenticationToken usuarioAutenticado() {
        return new UsernamePasswordAuthenticationToken(
                new UsuarioAutenticado(USUARIO_ID),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_PROFISSIONAL"))
        );
    }

    @Test
    @DisplayName("Deve redefinir senha do usuário autenticado")
    void deveRedefinirSenhaComSucesso() throws Exception {
        mockMvc.perform(post(REDEFINIR_SENHA_URL)
                        .with(authentication(usuarioAutenticado()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "novaSenha": "novaSenha123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Senha redefinida com sucesso"));

        verify(authService).redefinirSenha(eq(USUARIO_ID), any(RedefinirSenhaRequestDTO.class));
    }

    @Test
    @DisplayName("Deve rejeitar redefinição com senha curta")
    void deveRejeitarRedefinicaoComSenhaCurta() throws Exception {
        mockMvc.perform(post(REDEFINIR_SENHA_URL)
                        .with(authentication(usuarioAutenticado()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "novaSenha": "123"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    @DisplayName("Deve retornar 401 ao redefinir senha sem autenticação")
    void deveRetornarNaoAutorizadoAoRedefinirSenhaSemAutenticacao() throws Exception {
        mockMvc.perform(post(REDEFINIR_SENHA_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "novaSenha": "novaSenha123"
                                }
                                """))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(authService);
    }

    @Test
    @DisplayName("Deve retornar sucesso em /auth/me quando usuário está autenticado")
    void deveRetornarMeAutenticado() throws Exception {
        mockMvc.perform(get(ME_URL)
                        .with(authentication(usuarioAutenticado())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Autenticado"));
    }

    @Test
    @DisplayName("Deve retornar 401 em /auth/me sem autenticação")
    void deveRetornarNaoAutorizadoEmMeSemAutenticacao() throws Exception {
        mockMvc.perform(get(ME_URL))
                .andExpect(status().isUnauthorized());
    }
}
