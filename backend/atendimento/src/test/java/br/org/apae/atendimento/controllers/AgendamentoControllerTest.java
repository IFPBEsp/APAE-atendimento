package br.org.apae.atendimento.controllers;

import br.org.apae.atendimento.dtos.request.AgendamentoRequestDTO;
import br.org.apae.atendimento.dtos.response.AgendamentoResponseDTO;
import br.org.apae.atendimento.dtos.response.DiaAgendamentoResponseDTO;
import br.org.apae.atendimento.security.JwtService;
import br.org.apae.atendimento.security.TokenBlocklistService;
import br.org.apae.atendimento.security.UsuarioAutenticado;
import br.org.apae.atendimento.services.AgendamentoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AgendamentoController.class)
class AgendamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AgendamentoService service;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private TokenBlocklistService tokenBlocklistService;

    private UUID usuarioId;
    private UUID pacienteId;
    private UUID agendamentoId;
    private AgendamentoRequestDTO validRequest;
    private AgendamentoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        pacienteId = UUID.randomUUID();
        agendamentoId = UUID.randomUUID();

        validRequest = new AgendamentoRequestDTO(
                pacienteId,
                LocalDate.now().plusDays(5),
                LocalTime.of(14, 30)
        );

        responseDTO = new AgendamentoResponseDTO(
                agendamentoId,
                pacienteId,
                "Paciente Teste",
                usuarioId,
                "Profissional Teste",
                validRequest.data(),
                validRequest.hora(),
                "NUM-123",
                true,
                false
        );
    }

    private RequestPostProcessor authenticatedUser(UUID id) {
        UsuarioAutenticado usuario = new UsuarioAutenticado(id);
        Authentication auth = new UsernamePasswordAuthenticationToken(usuario, null, Collections.emptyList());
        return authentication(auth);
    }

    @Nested
    @DisplayName("Tests for Authentication security (401 Unauthorized)")
    class SecurityAuthenticationTests {

        @Test
        @DisplayName("Should return 401 Unauthorized when requesting POST /agendamento without authentication")
        void shouldReturn401WhenPostWithoutAuthentication() throws Exception {
            mockMvc.perform(post("/agendamento")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("Should return 401 Unauthorized when requesting GET /agendamento without authentication")
        void shouldReturn401WhenGetWithoutAuthentication() throws Exception {
            mockMvc.perform(get("/agendamento"))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("Should return 401 Unauthorized when requesting PUT /agendamento/{agendamentoId} without authentication")
        void shouldReturn401WhenPutWithoutAuthentication() throws Exception {
            mockMvc.perform(put("/agendamento/{agendamentoId}", agendamentoId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("Should return 401 Unauthorized when requesting DELETE /agendamento/{pacienteId}/{agendamentoId} without authentication")
        void shouldReturn401WhenDeleteWithoutAuthentication() throws Exception {
            mockMvc.perform(delete("/agendamento/{pacienteId}/{agendamentoId}", pacienteId, agendamentoId)
                            .with(csrf()))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(service);
        }

        @Test
        @DisplayName("Should return 401 Unauthorized when requesting PATCH /agendamento/{pacienteId}/{agendamentoId}/concluir without authentication")
        void shouldReturn401WhenPatchWithoutAuthentication() throws Exception {
            mockMvc.perform(patch("/agendamento/{pacienteId}/{agendamentoId}/concluir", pacienteId, agendamentoId)
                            .with(csrf()))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(service);
        }
    }

    @Nested
    @DisplayName("Tests for POST /agendamento")
    class PostAgendamentoTests {

        @Test
        @DisplayName("Should return 201 Created and response DTO when payload is valid")
        void shouldCreateAgendamentoSuccessfully() throws Exception {
            when(service.agendar(any(AgendamentoRequestDTO.class), eq(usuarioId))).thenReturn(responseDTO);

            mockMvc.perform(post("/agendamento")
                            .with(authenticatedUser(usuarioId))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(agendamentoId.toString()))
                    .andExpect(jsonPath("$.pacienteId").value(pacienteId.toString()))
                    .andExpect(jsonPath("$.nomePaciente").value("Paciente Teste"))
                    .andExpect(jsonPath("$.profissionalId").value(usuarioId.toString()))
                    .andExpect(jsonPath("$.status").value(true));

            verify(service).agendar(eq(validRequest), eq(usuarioId));
        }

        @Test
        @DisplayName("Should return 400 Bad Request when required fields are missing or data is in the past")
        void shouldReturn400WhenPayloadIsInvalid() throws Exception {
            AgendamentoRequestDTO invalidRequest = new AgendamentoRequestDTO(
                    null,
                    LocalDate.now().minusDays(1),
                    null
            );

            mockMvc.perform(post("/agendamento")
                            .with(authenticatedUser(usuarioId))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }
    }

    @Nested
    @DisplayName("Tests for PUT /agendamento/{agendamentoId}")
    class PutAgendamentoTests {

        @Test
        @DisplayName("Should return 200 OK when editing agendamento with valid payload")
        void shouldEditAgendamentoSuccessfully() throws Exception {
            when(service.editar(eq(agendamentoId), eq(usuarioId), any(AgendamentoRequestDTO.class)))
                    .thenReturn(responseDTO);

            mockMvc.perform(put("/agendamento/{agendamentoId}", agendamentoId)
                            .with(authenticatedUser(usuarioId))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(agendamentoId.toString()))
                    .andExpect(jsonPath("$.pacienteId").value(pacienteId.toString()));

            verify(service).editar(eq(agendamentoId), eq(usuarioId), eq(validRequest));
        }

        @Test
        @DisplayName("Should return 400 Bad Request when editing with invalid payload")
        void shouldReturn400WhenEditingWithInvalidPayload() throws Exception {
            AgendamentoRequestDTO invalidRequest = new AgendamentoRequestDTO(null, null, null);

            mockMvc.perform(put("/agendamento/{agendamentoId}", agendamentoId)
                            .with(authenticatedUser(usuarioId))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }
    }

    @Nested
    @DisplayName("Tests for GET /agendamento")
    class GetAgendamentoTests {

        @Test
        @DisplayName("Should list agendamentos with default pagination (page=0, size=10)")
        void shouldListAgendamentosWithDefaultPagination() throws Exception {
            Page<DiaAgendamentoResponseDTO> pageResponse = new PageImpl<>(
                    List.of(new DiaAgendamentoResponseDTO(LocalDate.now(), List.of(responseDTO))),
                    PageRequest.of(0, 10),
                    1
            );

            when(service.listarAgrupadoPorDia(usuarioId, null, 0, 10)).thenReturn(pageResponse);

            mockMvc.perform(get("/agendamento")
                            .with(authenticatedUser(usuarioId)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.number").value(0))
                    .andExpect(jsonPath("$.size").value(10))
                    .andExpect(jsonPath("$.totalElements").value(1));

            verify(service).listarAgrupadoPorDia(usuarioId, null, 0, 10);
        }

        @Test
        @DisplayName("Should parse ISO date parameter and custom pagination correctly")
        void shouldParseDateAndPaginationParameters() throws Exception {
            LocalDate filterDate = LocalDate.of(2026, 10, 25);
            Page<DiaAgendamentoResponseDTO> emptyPage = new PageImpl<>(
                    Collections.emptyList(),
                    PageRequest.of(1, 5),
                    0
            );

            when(service.listarAgrupadoPorDia(usuarioId, filterDate, 1, 5)).thenReturn(emptyPage);

            mockMvc.perform(get("/agendamento")
                            .with(authenticatedUser(usuarioId))
                            .param("data", "2026-10-25")
                            .param("page", "1")
                            .param("size", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.number").value(1))
                    .andExpect(jsonPath("$.size").value(5));

            verify(service).listarAgrupadoPorDia(usuarioId, filterDate, 1, 5);
        }

        @Test
        @DisplayName("Should return 400 Bad Request when date format is invalid")
        void shouldReturn400WhenDateFormatIsInvalid() throws Exception {
            mockMvc.perform(get("/agendamento")
                            .with(authenticatedUser(usuarioId))
                            .param("data", "25-10-2026"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }
    }

    @Nested
    @DisplayName("Tests for DELETE /agendamento/{pacienteId}/{agendamentoId}")
    class DeleteAgendamentoTests {

        @Test
        @DisplayName("Should return 200 OK and verify service call with correct IDs")
        void shouldDeleteAgendamentoSuccessfully() throws Exception {
            doNothing().when(service).deletar(usuarioId, pacienteId, agendamentoId);

            mockMvc.perform(delete("/agendamento/{pacienteId}/{agendamentoId}", pacienteId, agendamentoId)
                            .with(authenticatedUser(usuarioId))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Agendamento excluído"));

            verify(service).deletar(usuarioId, pacienteId, agendamentoId);
        }
    }

    @Nested
    @DisplayName("Tests for PATCH /agendamento/{pacienteId}/{agendamentoId}/concluir")
    class PatchConcluirAgendamentoTests {

        @Test
        @DisplayName("Should return 200 OK and verify service conclusion call")
        void shouldConcludeAgendamentoSuccessfully() throws Exception {
            doNothing().when(service).concluir(usuarioId, pacienteId, agendamentoId);

            mockMvc.perform(patch("/agendamento/{pacienteId}/{agendamentoId}/concluir", pacienteId, agendamentoId)
                            .with(authenticatedUser(usuarioId))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Agendamento concluído"));

            verify(service).concluir(usuarioId, pacienteId, agendamentoId);
        }
    }
}