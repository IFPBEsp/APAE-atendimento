package br.org.apae.atendimento.controllers;

import br.org.apae.atendimento.dtos.request.EsqueciSenhaRequestDTO;
import br.org.apae.atendimento.exceptions.handler.GlobalExceptionHandler;
import br.org.apae.atendimento.security.JwtAuthenticationFilter;
import br.org.apae.atendimento.security.JwtService;
import br.org.apae.atendimento.security.SecurityConfig;
import br.org.apae.atendimento.security.TokenBlocklistService;
import br.org.apae.atendimento.services.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private TokenBlocklistService tokenBlocklistService;

    @Test
    @DisplayName("Deve permitir redefinir senha em /auth/esqueci-senha sem token JWT e retornar 200 OK")
    void deveRedefinirSenhaComSucesso() throws Exception {
        EsqueciSenhaRequestDTO requestDTO = new EsqueciSenhaRequestDTO(
                "profissional@teste.local",
                "000.000.010-82",
                "novaSenha123"
        );

        doNothing().when(authService).esqueciSenha(any(EsqueciSenhaRequestDTO.class));

        mockMvc.perform(post("/auth/esqueci-senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Senha redefinida com sucesso"));

        verify(authService).esqueciSenha(requestDTO);
    }

    @Test
    @DisplayName("Deve retornar erro genérico quando E-mail ou CPF não conferirem")
    void deveRetornarErroGenericoQuandoDadosNaoConferem() throws Exception {
        EsqueciSenhaRequestDTO requestDTO = new EsqueciSenhaRequestDTO(
                "profissional@teste.local",
                "000.000.010-82",
                "novaSenha123"
        );

        doThrow(new BadCredentialsException("Os dados informados não conferem"))
                .when(authService).esqueciSenha(any(EsqueciSenhaRequestDTO.class));

        mockMvc.perform(post("/auth/esqueci-senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Os dados informados não conferem"));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando o e-mail for inválido")
    void deveRetornarBadRequestQuandoEmailInvalido() throws Exception {
        EsqueciSenhaRequestDTO requestDTO = new EsqueciSenhaRequestDTO(
                "email-invalido",
                "000.000.010-82",
                "novaSenha123"
        );

        mockMvc.perform(post("/auth/esqueci-senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).esqueciSenha(any());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando o CPF for inválido")
    void deveRetornarBadRequestQuandoCpfInvalido() throws Exception {
        EsqueciSenhaRequestDTO requestDTO = new EsqueciSenhaRequestDTO(
                "profissional@teste.local",
                "123.456.789-00",
                "novaSenha123"
        );

        mockMvc.perform(post("/auth/esqueci-senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).esqueciSenha(any());
    }
}
