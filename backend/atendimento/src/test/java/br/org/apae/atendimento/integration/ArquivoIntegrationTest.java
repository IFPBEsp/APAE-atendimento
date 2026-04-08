package br.org.apae.atendimento.integration;

import br.org.apae.atendimento.repositories.AnexoRepository;
import br.org.apae.atendimento.services.ArquivoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ArquivoIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    ArquivoService serviceMock = Mockito.mock(ArquivoService.class);

    @Autowired
    AnexoRepository anexoRepository;

    UUID pacienteId = UUID.randomUUID();

    @BeforeEach
    void clean() {
        anexoRepository.deleteAll();
    }

    private MockMultipartFile mockFile(String name, String type) {
        return new MockMultipartFile("file", name, type, "dummy".getBytes(StandardCharsets.UTF_8));
    }

    private MockMultipartFile metadata(LocalDate data, String titulo, String descricao, Long tipo) {
        String json = """
            {"data":"%s","tipoArquivo":%d,"pacienteId":"%s","titulo":"%s","descricao":"%s"}
            """.formatted(data, tipo, pacienteId, titulo, descricao);
        return new MockMultipartFile("metadata", "metadata.json", "application/json", json.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("Título nulo -> 400 e nada persistido")
    void tituloNulo() throws Exception {
        MockMultipartFile file = mockFile("ok.pdf", "application/pdf");
        MockMultipartFile meta = new MockMultipartFile("metadata", "metadata.json", "application/json",
                """
                {"data":"2024-01-01","tipoArquivo":2,"pacienteId":"%s","titulo":null,"descricao":"ok"}
                """.formatted(pacienteId).getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/arquivo")
                        .file(file)
                        .file(meta)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());

        assertThat(anexoRepository.count()).isZero();
    }

    @Test
    @DisplayName("Data fora do intervalo -> 400")
    void dataForaIntervalo() throws Exception {
        MockMultipartFile file = mockFile("ok.pdf", "application/pdf");
        MockMultipartFile meta = metadata(LocalDate.now().minusYears(31), "Titulo", "Desc", 2L);

        mockMvc.perform(multipart("/arquivo")
                        .file(file)
                        .file(meta)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());

        assertThat(anexoRepository.count()).isZero();
    }

    @Test
    @DisplayName("MIME inválido -> 400")
    void mimeInvalido() throws Exception {
        MockMultipartFile file = mockFile("musica.mp3", "audio/mp3");
        MockMultipartFile meta = metadata(LocalDate.now(), "Titulo", "Desc", 2L);

        mockMvc.perform(multipart("/arquivo")
                        .file(file)
                        .file(meta)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());

        assertThat(anexoRepository.count()).isZero();
    }

    @Test
    @DisplayName("JSON corrompido -> 400")
    void jsonCorrompido() throws Exception {
        MockMultipartFile file = mockFile("ok.pdf", "application/pdf");
        MockMultipartFile meta = new MockMultipartFile("metadata", "metadata.json", "application/json",
                "{data:2024-01-01".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/arquivo")
                        .file(file)
                        .file(meta)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());

        assertThat(anexoRepository.count()).isZero();
    }
}