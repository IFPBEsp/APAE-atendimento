package br.org.apae.atendimento.services;

import br.org.apae.atendimento.dtos.request.ArquivoRequestDTO;
import br.org.apae.atendimento.entities.Arquivo;
import br.org.apae.atendimento.entities.Paciente;
import br.org.apae.atendimento.entities.ProfissionalSaude;
import br.org.apae.atendimento.entities.TipoArquivo;
import br.org.apae.atendimento.repositories.AnexoRepository;
import br.org.apae.atendimento.repositories.PacienteRepository;
import br.org.apae.atendimento.repositories.ProfissionalSaudeRepository;
import br.org.apae.atendimento.repositories.TipoArquivoRepository;
import br.org.apae.atendimento.services.storage.ObjectStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ArquivoIntegrationTest {

    @Autowired
    private ArquivoService arquivoService;

    @Autowired
    private AnexoRepository anexoRepository;

    @Autowired
    private TipoArquivoRepository tipoArquivoRepository;

    @MockitoBean
    private ObjectStorageService storageService;

    private UUID profissionalId = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private UUID pacienteId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

    @BeforeEach
    void setUp() {
        when(storageService.uploadArquivo(any(), any())).thenReturn("http://mock-url");
    }

    @Test
    @DisplayName("Deve garantir rollback quando ocorre falha de integridade (Título Nulo)")
    void deveGarantirRollbackEmFalhaDeIntegridade() {
        MockMultipartFile file = new MockMultipartFile("file", "teste.pdf", "application/pdf", "conteudo".getBytes());

        Arquivo arquivoInvalido = new Arquivo();
        arquivoInvalido.setObjectName("teste/falha");
        arquivoInvalido.setTitulo(null); 
        arquivoInvalido.setDescricao("Desc");
        arquivoInvalido.setData(LocalDate.now());
        arquivoInvalido.setNomeArquivo("teste.pdf");
        
        assertThrows(DataIntegrityViolationException.class, () -> {
            anexoRepository.saveAndFlush(arquivoInvalido);
        });
    }

    @Test
    @DisplayName("Deve persistir arquivo com sucesso usando H2")
    void devePersistirComSucesso() {
        MockMultipartFile file = new MockMultipartFile("file", "relatorio.pdf", "application/pdf", "pdf content".getBytes());
        ArquivoRequestDTO requestDTO = new ArquivoRequestDTO(
                LocalDate.now(),
                1L,
                pacienteId,
                "Relatório Semestral",
                "Descrição do relatório detalhada"
        );

        if (!tipoArquivoRepository.existsById(1L)) {
            tipoArquivoRepository.save(new TipoArquivo(1L, "Anexo"));
        }

        var response = arquivoService.salvar(file, requestDTO, profissionalId);

        assertNotNull(response);
        assertTrue(anexoRepository.existsById(response.objectName()));
        
        Arquivo persistido = anexoRepository.findById(response.objectName()).get();
        assertEquals("relatório semestral", persistido.getTitulo());
        assertNotNull(persistido.getPaciente());
        assertNotNull(persistido.getProfissional());
    }
}
