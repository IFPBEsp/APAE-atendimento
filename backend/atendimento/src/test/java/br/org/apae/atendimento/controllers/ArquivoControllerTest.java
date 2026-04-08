package br.org.apae.atendimento.controllers;

import br.org.apae.atendimento.dtos.request.ArquivoRequestDTO;
import br.org.apae.atendimento.mappers.ArquivoMapper;
import br.org.apae.atendimento.security.UsuarioAutenticado;
import br.org.apae.atendimento.services.ArquivoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ArquivoController.class)
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArquivoController.class)
@AutoConfigureMockMvc(addFilters = false)
class ArquivoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ArquivoService service;

    // MockAuthenticationFilter é necessário para o contexto de segurança funcionar
    @MockitoBean
    private MockAuthenticationFilter mockAuthFilter;

    private MockMultipartFile metadataValida() throws Exception {
        String json = objectMapper.writeValueAsString(new br.org.apae.atendimento.dtos.request.ArquivoRequestDTO(
                LocalDate.now(),
                1L,
                UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
                "Título Válido",
                "Descrição válida do arquivo"
        ));
        return new MockMultipartFile("metadata", "", "application/json", json.getBytes());
    }

    @Test
    @DisplayName("Deve retornar 201 ao enviar arquivo válido")
    void deveRetornar201ParaUploadValido() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "foto.jpg", "image/jpeg", "conteudo".getBytes());

        when(service.salvar(any(), any(), any())).thenReturn(
                new ArquivoResponseDTO("obj/path", "http://url", LocalDate.now(), "foto.jpg", "título válido", "descrição")
        );

        mockMvc.perform(multipart("/arquivo")
                        .file(file)
                        .file(metadataValida()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.objectName").exists());
    }

    @Test
    @DisplayName("Deve retornar 400 ao enviar metadata com título inválido (apenas números)")
    void deveRetornar400TituloApenasNumeros() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "foto.jpg", "image/jpeg", "conteudo".getBytes());

        String json = objectMapper.writeValueAsString(new br.org.apae.atendimento.dtos.request.ArquivoRequestDTO(
                LocalDate.now(), 1L,
                UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
                "123456",      // inválido
                "Descrição válida"
        ));
        MockMultipartFile metadata = new MockMultipartFile("metadata", "", "application/json", json.getBytes());

        mockMvc.perform(multipart("/arquivo").file(file).file(metadata))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("Deve retornar 400 ao enviar metadata com data futura")
    void deveRetornar400DataFutura() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "foto.jpg", "image/jpeg", "conteudo".getBytes());

        String json = objectMapper.writeValueAsString(new br.org.apae.atendimento.dtos.request.ArquivoRequestDTO(
                LocalDate.now().plusDays(1),  // inválido
                1L,
                UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
                "Título Válido",
                "Descrição válida"
        ));
        MockMultipartFile metadata = new MockMultipartFile("metadata", "", "application/json", json.getBytes());

        mockMvc.perform(multipart("/arquivo").file(file).file(metadata))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 400 ao enviar metadata com título nulo")
    void deveRetornar400TituloNulo() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "foto.jpg", "image/jpeg", "conteudo".getBytes());

        String json = objectMapper.writeValueAsString(new br.org.apae.atendimento.dtos.request.ArquivoRequestDTO(
                LocalDate.now(), 1L,
                UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
                null,          // inválido
                "Descrição válida"
        ));
        MockMultipartFile metadata = new MockMultipartFile("metadata", "", "application/json", json.getBytes());

        mockMvc.perform(multipart("/arquivo").file(file).file(metadata))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 400 ao enviar JSON corrompido na metadata")
    void deveRetornar400JsonCorrompido() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "foto.jpg", "image/jpeg", "conteudo".getBytes());
        MockMultipartFile metadata = new MockMultipartFile("metadata", "", "application/json",
                "{ json corrompido }".getBytes());

        mockMvc.perform(multipart("/arquivo").file(file).file(metadata))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 500 sem expor stacktrace ao ocorrer falha crítica no serviço")
    void deveRetornar500SemExporStacktrace() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "foto.jpg", "image/jpeg", "conteudo".getBytes());

        when(service.salvar(any(), any(), any()))
                .thenThrow(new RuntimeException("Erro crítico simulado"));

        mockMvc.perform(multipart("/arquivo")
                        .file(file)
                        .file(metadataValida()))
                .andExpect(status().isInternalServerError())
                // Garante que não há stacktrace ou detalhes técnicos no body
                .andExpect(jsonPath("$.message").value("Ocorreu um erro interno inesperado no servidor."))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("RuntimeException"))));
    }

    @Test
    @DisplayName("Deve retornar 404 quando TipoArquivo não existe")
    void deveRetornar404TipoArquivoInexistente() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "foto.jpg", "image/jpeg", "conteudo".getBytes());

        when(service.salvar(any(), any(), any()))
                .thenThrow(new TipoArquivoNotFoundException("O tipo de arquivo selecionado é inválido"));

        mockMvc.perform(multipart("/arquivo")
                        .file(file)
                        .file(metadataValida()))
                .andExpect(status().isNotFound());
    @MockBean
    private ArquivoService arquivoService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUpSecurity() {
        UsuarioAutenticado usuarioMock = mock(UsuarioAutenticado.class);

        when(usuarioMock.getId()).thenReturn(UUID.randomUUID());

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuarioMock, null, null)
        );
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao receber metadados (JSON) malformados")
    void deveRetornar400ParaMetadadosMalformados() throws Exception {
        MockMultipartFile filePart = new MockMultipartFile(
                "file", "relatorio.pdf", MediaType.APPLICATION_PDF_VALUE, "dummy content".getBytes());

        MockMultipartFile metadataPart = new MockMultipartFile(
                "metadata", "", MediaType.APPLICATION_JSON_VALUE, "{ \"titulo\": \"Falta fechar".getBytes());

        mockMvc.perform(multipart("/arquivo")
                        .file(filePart)
                        .file(metadataPart))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 415 Unsupported Media Type para arquivos não autorizados (.exe)")
    void deveRetornar415ParaMimeTypeNaoSuportado() throws Exception {
        MockMultipartFile arquivoMalicioso = new MockMultipartFile(
                "file", "virus.exe", "application/x-msdownload", "conteudo perigoso".getBytes());

        ArquivoRequestDTO dtoValido = new ArquivoRequestDTO(
                LocalDate.now(),
                1L,
                UUID.randomUUID(),
                "Relatório de Teste",
                "Descrição válida"
        );

        String jsonValido = objectMapper.writeValueAsString(dtoValido);

        MockMultipartFile metadataPart = new MockMultipartFile(
                "metadata", "", MediaType.APPLICATION_JSON_VALUE, jsonValido.getBytes());

        when(arquivoService.salvar(any(), any(), any())).thenThrow(
                new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Formato de arquivo não suportado.")
        );

        mockMvc.perform(multipart("/arquivo")
                        .file(arquivoMalicioso)
                        .file(metadataPart))
                        .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("Deve retornar 500 sem expor stacktrace em caso de falha crítica interna")
    void deveRetornar500SemExporStacktrace() throws Exception {
        MockMultipartFile filePart = new MockMultipartFile(
                "file", "relatorio.pdf", MediaType.APPLICATION_PDF_VALUE, "dummy content".getBytes());

        ArquivoRequestDTO dtoValido = new ArquivoRequestDTO(
                LocalDate.now(),
                1L,
                UUID.randomUUID(),
                "Relatório de Teste",
                "Descrição válida"
        );

        String jsonValido = objectMapper.writeValueAsString(dtoValido);

        MockMultipartFile metadataPart = new MockMultipartFile(
                "metadata", "", MediaType.APPLICATION_JSON_VALUE, jsonValido.getBytes());

        when(arquivoService.salvar(any(), any(), any())).thenThrow(new RuntimeException("Erro de conexão com storage"));

        mockMvc.perform(multipart("/arquivo")
                        .file(filePart)
                        .file(metadataPart))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.stacktrace").doesNotExist())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.not("Erro de conexão com storage")));
    }
}