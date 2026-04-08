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