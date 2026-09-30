package br.org.apae.atendimento.integration;

import br.org.apae.atendimento.repositories.AnexoRepository;
import br.org.apae.atendimento.services.ArquivoService;
import br.org.apae.atendimento.services.storage.ObjectStorageService;
import br.org.apae.atendimento.services.storage.PresignedUrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class ArquivoIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    ArquivoService serviceMock = Mockito.mock(ArquivoService.class);

    @Autowired
    AnexoRepository anexoRepository;

    @Autowired
    ArquivoService arquivoService;

    @MockitoBean
    ObjectStorageService storageService;

    @MockitoBean
    PresignedUrlService urlService;

    UUID pacienteId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

    @BeforeEach
    void clean() {
        anexoRepository.deleteAll();
        when(storageService.uploadArquivo(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn("http://mock-url");
        when(urlService.gerarUrlPreAssinada(ArgumentMatchers.any())).thenReturn("http://mock-presigned-url");
    }

    private void seedArquivos(int quantidade, Long tipoId) {
        for (int i = 0; i < quantidade; i++) {
            MockMultipartFile file = mockFile("arquivo-" + i + ".pdf", "application/pdf");
            arquivoService.salvar(
                    file,
                    new br.org.apae.atendimento.dtos.request.ArquivoRequestDTO(
                            LocalDate.now(), tipoId, pacienteId,
                            "Titulo " + i, "Descricao " + i
                    ),
                    UUID.fromString("44444444-4444-4444-4444-444444444444")
            );
        }
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
    @DisplayName("Titulo nulo -> 400 e nada persistido")
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
    @DisplayName("MIME invalido -> 400")
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
    @DisplayName("Deve retornar arquivos paginados respeitando page (base zero) e limit")
    void deveListarArquivosPaginados() throws Exception {
        seedArquivos(5, 1L);

        mockMvc.perform(get("/arquivo/{pacienteId}/{tipoId}", pacienteId, 1L)
                        .param("page", "0")
                        .param("limit", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.paginationMetaDTO.page").value(0))
                .andExpect(jsonPath("$.paginationMetaDTO.limit").value(2))
                .andExpect(jsonPath("$.paginationMetaDTO.totalItems").value(5))
                .andExpect(jsonPath("$.paginationMetaDTO.totalPages").value(3))
                .andExpect(jsonPath("$.paginationMetaDTO.hasNextPage").value(true))
                .andExpect(jsonPath("$.paginationMetaDTO.hasPreviousPage").value(false));
    }

    @Test
    @DisplayName("Deve usar page=0 e limit=10 como padrão quando não informados")
    void deveUsarPaginacaoPadraoQuandoParametrosAusentes() throws Exception {
        seedArquivos(3, 1L);

        mockMvc.perform(get("/arquivo/{pacienteId}/{tipoId}", pacienteId, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(3))
                .andExpect(jsonPath("$.paginationMetaDTO.page").value(0))
                .andExpect(jsonPath("$.paginationMetaDTO.limit").value(10))
                .andExpect(jsonPath("$.paginationMetaDTO.hasNextPage").value(false));
    }

    @Test
    @DisplayName("Deve retornar página vazia quando page excede o total de páginas")
    void deveRetornarPaginaVaziaAlemDoTotal() throws Exception {
        seedArquivos(2, 1L);

        mockMvc.perform(get("/arquivo/{pacienteId}/{tipoId}", pacienteId, 1L)
                        .param("page", "5")
                        .param("limit", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0))
                .andExpect(jsonPath("$.paginationMetaDTO.totalItems").value(2));
    }

    @Test
    @DisplayName("Deve ordenar arquivos por data decrescente")
    void deveOrdenarPorDataDecrescente() throws Exception {
        UUID profissionalIdMock = UUID.fromString("44444444-4444-4444-4444-444444444444");

        arquivoService.salvar(mockFile("antigo.pdf", "application/pdf"),
                new br.org.apae.atendimento.dtos.request.ArquivoRequestDTO(
                        LocalDate.now().minusDays(10), 1L, pacienteId, "Antigo", "Desc"),
                profissionalIdMock);
        arquivoService.salvar(mockFile("recente.pdf", "application/pdf"),
                new br.org.apae.atendimento.dtos.request.ArquivoRequestDTO(
                        LocalDate.now(), 1L, pacienteId, "Recente", "Desc"),
                profissionalIdMock);

        mockMvc.perform(get("/arquivo/{pacienteId}/{tipoId}", pacienteId, 1L)
                        .param("page", "0")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].titulo").value("recente"))
                .andExpect(jsonPath("$.data[1].titulo").value("antigo"));
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