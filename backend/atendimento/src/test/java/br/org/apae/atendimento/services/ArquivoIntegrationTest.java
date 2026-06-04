package br.org.apae.atendimento.services;

import br.org.apae.atendimento.dtos.request.ArquivoRequestDTO;
import br.org.apae.atendimento.entities.Arquivo;
import br.org.apae.atendimento.integration.AbstractIntegrationTest;
import br.org.apae.atendimento.repositories.AnexoRepository;
import br.org.apae.atendimento.services.storage.ObjectStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Transactional
class ArquivoIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ArquivoService arquivoService;

    @Autowired
    private AnexoRepository anexoRepository;

    @MockitoBean
    private ObjectStorageService storageService;

    private final UUID profissionalId = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private final UUID pacienteId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

    @BeforeEach
    void setUp() {
        when(storageService.uploadArquivo(any(), any())).thenReturn("http://mock-url");
    }

    @Test
    @DisplayName("Deve garantir rollback quando ocorre falha de integridade com titulo nulo")
    void deveGarantirRollbackEmFalhaDeIntegridade() {
        Arquivo arquivoInvalido = new Arquivo();
        arquivoInvalido.setObjectName("teste/falha");
        arquivoInvalido.setTitulo(null);
        arquivoInvalido.setDescricao("Desc");
        arquivoInvalido.setData(LocalDate.now());
        arquivoInvalido.setNomeArquivo("teste.pdf");

        assertThrows(DataIntegrityViolationException.class, () -> anexoRepository.saveAndFlush(arquivoInvalido));
    }

    @Test
    @DisplayName("Deve persistir arquivo com sucesso usando PostgreSQL e Flyway")
    void devePersistirComSucesso() {
        MockMultipartFile file = new MockMultipartFile("file", "relatorio.pdf", "application/pdf", "pdf content".getBytes());
        ArquivoRequestDTO requestDTO = new ArquivoRequestDTO(
                LocalDate.now(),
                1L,
                pacienteId,
                "Relatorio Semestral",
                "Descricao do relatorio detalhada"
        );

        var response = arquivoService.salvar(file, requestDTO, profissionalId);

        assertNotNull(response);
        assertEquals("http://mock-url", response.presignedUrl());
        assertTrue(anexoRepository.existsById(response.objectName()));

        Arquivo persistido = anexoRepository.findById(response.objectName()).orElseThrow();
        assertEquals("relatorio semestral", persistido.getTitulo());
        assertEquals(pacienteId, persistido.getPacienteId());
        assertEquals(profissionalId, persistido.getProfissionalId());
    }
}
