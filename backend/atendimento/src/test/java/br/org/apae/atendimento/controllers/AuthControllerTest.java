package br.org.apae.atendimento.controllers;

import br.org.apae.atendimento.dtos.request.LoginRequestDTO;
import br.org.apae.atendimento.dtos.response.AutenticacaoResponseDTO;
import br.org.apae.atendimento.security.JwtService;
import br.org.apae.atendimento.security.TokenBlocklistService;
import br.org.apae.atendimento.services.AuthService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes da camada Web do AuthController com os filtros de segurança desligados.
 * Cenários que dependem da cadeia de filtros do Spring Security ficam em
 * AuthControllerSecurityTest.
 */
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "jwt.expiration.minutes=30",
        "jwt.cookie.secure=false"
})
class AuthControllerTest {

    private static final String LOGIN_URL = "/auth/login";
    private static final String LOGOUT_URL = "/auth/logout";
    private static final String ME_URL = "/auth/me";
    private static final String TOKEN_VALIDO = "jwt-teste";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private TokenBlocklistService tokenBlocklistService;

    // ---------------------------------------------------------------- LOGIN

    @Test
    @DisplayName("Deve realizar login e configurar cookie de autenticação")
    void deveRealizarLoginComSucesso() throws Exception {
        when(authService.autenticar(any(LoginRequestDTO.class)))
                .thenReturn(new AutenticacaoResponseDTO(TOKEN_VALIDO, false, "/inicio"));

        mockMvc.perform(post(LOGIN_URL)
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
                        allOf(
                                containsString("token=" + TOKEN_VALIDO),
                                containsString("HttpOnly"),
                                containsString("Path=/"),
                                containsString("SameSite=Strict"),
                                containsString("Max-Age=1800")
                        )));

        verify(authService).autenticar(any(LoginRequestDTO.class));
    }

    @Test
    @DisplayName("Deve rejeitar login com e-mail inválido")
    void deveRejeitarLoginComEmailInvalido() throws Exception {
        mockMvc.perform(post(LOGIN_URL)
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
        mockMvc.perform(post(LOGIN_URL)
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

    static Stream<Arguments> payloadsDeLoginInvalidos() {
        return Stream.of(
                Arguments.of("corpo vazio (campos ausentes)", "{}"),
                Arguments.of("e-mail e senha nulos", "{\"email\": null, \"password\": null}"),
                Arguments.of("e-mail e senha vazios", "{\"email\": \"\", \"password\": \"\"}"),
                Arguments.of("e-mail em branco", "{\"email\": \"   \", \"password\": \"senha123\"}"),
                Arguments.of("senha ausente", "{\"email\": \"usuario@apae.org.br\"}"),
                Arguments.of("senha nula", "{\"email\": \"usuario@apae.org.br\", \"password\": null}"),
                Arguments.of("senha vazia", "{\"email\": \"usuario@apae.org.br\", \"password\": \"\"}")
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("payloadsDeLoginInvalidos")
    @DisplayName("Deve rejeitar login com campos nulos ou vazios")
    void deveRejeitarLoginComCamposNulosOuVazios(String descricao, String payload) throws Exception {
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist(HttpHeaders.SET_COOKIE));

        verifyNoInteractions(authService);
    }

    @Test
    @DisplayName("Deve retornar 401 e não definir cookie quando as credenciais são inválidas")
    void deveRetornarNaoAutorizadoComCredenciaisInvalidas() throws Exception {
        when(authService.autenticar(any(LoginRequestDTO.class)))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "usuario@apae.org.br",
                                  "password": "senhaErrada123"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(header().doesNotExist(HttpHeaders.SET_COOKIE));

        verify(authService).autenticar(any(LoginRequestDTO.class));
    }

    // --------------------------------------------------------------- LOGOUT

    @Test
    @DisplayName("Deve revogar token e limpar cookie ao realizar logout")
    void deveRealizarLogoutComTokenValido() throws Exception {
        when(jwtService.tokenValido(TOKEN_VALIDO)).thenReturn(true);
        when(jwtService.extrairJti(TOKEN_VALIDO)).thenReturn("jti-teste");
        when(jwtService.extrairExpiracao(TOKEN_VALIDO)).thenReturn(new Date());

        mockMvc.perform(post(LOGOUT_URL)
                        .cookie(new Cookie("token", TOKEN_VALIDO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Logout realizado com sucesso"))
                .andExpect(header().string(HttpHeaders.SET_COOKIE,
                        allOf(
                                containsString("token="),
                                containsString("Max-Age=0"),
                                containsString("HttpOnly"),
                                containsString("SameSite=Strict")
                        )));

        verify(tokenBlocklistService).revogar(eq("jti-teste"), any(Date.class));
    }

    @Test
    @DisplayName("Deve limpar cookie no logout mesmo sem token")
    void deveRealizarLogoutSemToken() throws Exception {
        mockMvc.perform(post(LOGOUT_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(header().string(HttpHeaders.SET_COOKIE,
                        allOf(
                                containsString("token="),
                                containsString("Max-Age=0")
                        )));

        verifyNoInteractions(jwtService);
        verifyNoInteractions(tokenBlocklistService);
    }

    @Test
    @DisplayName("Deve limpar cookie e não revogar nada quando o token é inválido")
    void deveRealizarLogoutComTokenInvalido() throws Exception {
        when(jwtService.tokenValido("token-invalido")).thenReturn(false);

        mockMvc.perform(post(LOGOUT_URL)
                        .cookie(new Cookie("token", "token-invalido")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(header().string(HttpHeaders.SET_COOKIE,
                        allOf(
                                containsString("token="),
                                containsString("Max-Age=0")
                        )));

        verify(jwtService, never()).extrairJti(any());
        verify(tokenBlocklistService, never()).revogar(any(), any());
    }

    @ParameterizedTest(name = "JTI = [{0}]")
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("Deve limpar cookie e não revogar nada quando o JTI é nulo ou em branco")
    void deveRealizarLogoutComJtiNuloOuEmBranco(String jti) throws Exception {
        when(jwtService.tokenValido(TOKEN_VALIDO)).thenReturn(true);
        when(jwtService.extrairJti(TOKEN_VALIDO)).thenReturn(jti);

        mockMvc.perform(post(LOGOUT_URL)
                        .cookie(new Cookie("token", TOKEN_VALIDO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(header().string(HttpHeaders.SET_COOKIE,
                        allOf(
                                containsString("token="),
                                containsString("Max-Age=0")
                        )));

        verify(jwtService, never()).extrairExpiracao(any());
        verify(tokenBlocklistService, never()).revogar(any(), any());
    }

    // ------------------------------------------------------------------- ME

    @Test
    @DisplayName("Deve retornar não autenticado quando não existe autenticação")
    void deveRetornarMeNaoAutenticado() throws Exception {
        mockMvc.perform(get(ME_URL))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Não autenticado"));
    }

    @Test
    @DisplayName("Deve retornar não autenticado quando a autenticação não está marcada como autenticada")
    void deveRetornarMeComAutenticacaoNaoAutenticada() throws Exception {
        UsernamePasswordAuthenticationToken naoAutenticado =
                UsernamePasswordAuthenticationToken.unauthenticated("usuario", "senha");

        mockMvc.perform(get(ME_URL)
                        .principal(naoAutenticado))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Não autenticado"));
    }
}
