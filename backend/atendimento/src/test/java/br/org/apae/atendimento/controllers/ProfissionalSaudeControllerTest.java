package br.org.apae.atendimento.controllers;

import br.org.apae.atendimento.dtos.response.PacienteOptionDTO;
import br.org.apae.atendimento.dtos.response.PacienteResponseDTO;
import br.org.apae.atendimento.dtos.response.ProfissionalResponseDTO;
import br.org.apae.atendimento.exceptions.handler.GlobalExceptionHandler;
import br.org.apae.atendimento.exceptions.notfound.ProfissionalSaudeNotFoundException;
import br.org.apae.atendimento.security.JwtAuthenticationFilter;
import br.org.apae.atendimento.security.JwtService;
import br.org.apae.atendimento.security.SecurityConfig;
import br.org.apae.atendimento.security.TokenBlocklistService;
import br.org.apae.atendimento.services.ProfissionalSaudeService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ProfissionalSaudeController.class, properties = "auth.mock.enabled=false")
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        GlobalExceptionHandler.class
})
class ProfissionalSaudeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProfissionalSaudeService profissionalSaudeService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private TokenBlocklistService tokenBlocklistService;

    private final UUID profissionalId =
            UUID.fromString("11111111-1111-1111-1111-111111111111");

    private final UUID pacienteId =
            UUID.fromString("22222222-2222-2222-2222-222222222222");

    private final String token = "token-de-teste";

    @BeforeEach
    void configurarAutenticacao() {
        when(jwtService.tokenValido(token)).thenReturn(true);
        when(jwtService.extrairJti(token)).thenReturn("jti-de-teste");
        when(jwtService.extrairSubject(token)).thenReturn(profissionalId.toString());
        when(jwtService.extrairRoles(token)).thenReturn(List.of("ROLE_PROFISSIONAL"));
        when(tokenBlocklistService.estaRevogado("jti-de-teste")).thenReturn(false);
    }

    private Cookie cookieAutenticacao() {
        return new Cookie("token", token);
    }

    @Test
    @DisplayName("GET /profissionais deve retornar o profissional autenticado")
    void deveBuscarProfissionalAutenticado() throws Exception {
        ProfissionalResponseDTO profissional = new ProfissionalResponseDTO(
                profissionalId,
                "Profissional Teste",
                "profissional@teste.com",
                "83999999999"
        );

        when(profissionalSaudeService.getProfissionalByIdDTO(profissionalId))
                .thenReturn(profissional);

        mockMvc.perform(get("/profissionais")
                        .cookie(cookieAutenticacao()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(profissionalId.toString()))
                .andExpect(jsonPath("$.nomeCompleto").value("Profissional Teste"))
                .andExpect(jsonPath("$.email").value("profissional@teste.com"))
                .andExpect(jsonPath("$.contato").value("83999999999"));

        verify(profissionalSaudeService).getProfissionalByIdDTO(profissionalId);
    }

    @Test
    @DisplayName("GET /profissionais deve retornar 404 quando o profissional não existe")
    void deveRetornar404QuandoProfissionalNaoExiste() throws Exception {
        when(profissionalSaudeService.getProfissionalByIdDTO(profissionalId))
                .thenThrow(new ProfissionalSaudeNotFoundException(
                        "Profissional de saúde não encontrado."
                ));

        mockMvc.perform(get("/profissionais")
                        .cookie(cookieAutenticacao()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Profissional de saúde não encontrado."));
    }

    @Test
    @DisplayName("GET /profissionais deve retornar 401 sem autenticação")
    void deveRetornar401AoBuscarProfissionalSemAutenticacao() throws Exception {
        mockMvc.perform(get("/profissionais"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /profissionais/pacientes deve listar os pacientes do profissional")
    void deveListarPacientesDoProfissional() throws Exception {
        PacienteResponseDTO paciente = new PacienteResponseDTO(
                pacienteId,
                "Paciente Teste",
                LocalDate.of(2015, 5, 10),
                "Rua de Teste, 123",
                "83988888888",
                Set.of(),
                Set.of(),
                "12345678901",
                "https://teste.com/foto"
        );

        when(profissionalSaudeService.getPacientesDoProfissional(profissionalId))
                .thenReturn(List.of(paciente));

        mockMvc.perform(get("/profissionais/pacientes")
                        .cookie(cookieAutenticacao()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(pacienteId.toString()))
                .andExpect(jsonPath("$[0].nomeCompleto").value("Paciente Teste"))
                .andExpect(jsonPath("$[0].fotoPreAssinada")
                        .value("https://teste.com/foto"));

        verify(profissionalSaudeService).getPacientesDoProfissional(profissionalId);
    }

    @Test
    @DisplayName("GET /profissionais/pacientes deve retornar 401 sem autenticação")
    void deveRetornar401AoListarPacientesSemAutenticacao() throws Exception {
        mockMvc.perform(get("/profissionais/pacientes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /profissionais/pacientes-option deve retornar opções de pacientes")
    void deveListarOpcoesDePacientes() throws Exception {
        PacienteOptionDTO paciente = new PacienteOptionDTO(
                pacienteId,
                "Paciente Teste"
        );

        when(profissionalSaudeService.getPacienteOption(profissionalId))
                .thenReturn(List.of(paciente));

        mockMvc.perform(get("/profissionais/pacientes-option")
                        .cookie(cookieAutenticacao()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(pacienteId.toString()))
                .andExpect(jsonPath("$[0].nome").value("Paciente Teste"));

        verify(profissionalSaudeService).getPacienteOption(profissionalId);
    }

    @Test
    @DisplayName("GET /profissionais/pacientes-option deve retornar 401 sem autenticação")
    void deveRetornar401AoListarOpcoesSemAutenticacao() throws Exception {
        mockMvc.perform(get("/profissionais/pacientes-option"))
                .andExpect(status().isUnauthorized());
    }
}
