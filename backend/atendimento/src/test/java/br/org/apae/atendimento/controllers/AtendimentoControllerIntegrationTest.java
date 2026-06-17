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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class AtendimentoControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Deve criar e listar atendimentos para paciente vinculado ao profissional autenticado")
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

        mockMvc.perform(
                get("/atendimentos/{pacienteId}", pacienteId)
        ).andExpect(status().isOk());
    }
}