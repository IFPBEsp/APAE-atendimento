package br.org.apae.atendimento.controllers;

import br.org.apae.atendimento.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class AtendimentoControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Deve criar e listar atendimentos para paciente vinculado ao profissional autenticado com paginação")
    void deveCriarEListarAtendimentosComProfissionalAutenticado() throws Exception {
        String pacienteId = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa";

        String payload = """
        {
          "pacienteId": "%s",
          "relatorio": [{"titulo": "Titulo 1", "descricao": "Descricao 1"}],
          "data": "10-05-2026",
          "hora": "10:00"
        }
        """.formatted(pacienteId);

        mockMvc.perform(
                post("/atendimentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload)
        ).andExpect(status().isCreated());

        // Testando listagem com parâmetros default (page=0, size=10)
        mockMvc.perform(
                get("/atendimentos/{pacienteId}", pacienteId)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.totalElements").isNumber())
        .andExpect(jsonPath("$.totalPages").isNumber())
        .andExpect(jsonPath("$.number").value(0))
        .andExpect(jsonPath("$.size").value(10));

        // Testando listagem com parâmetros explícitos (page=0, size=5)
        mockMvc.perform(
                get("/atendimentos/{pacienteId}", pacienteId)
                        .param("page", "0")
                        .param("size", "5")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.size").value(5))
        .andExpect(jsonPath("$.number").value(0));
    }
}