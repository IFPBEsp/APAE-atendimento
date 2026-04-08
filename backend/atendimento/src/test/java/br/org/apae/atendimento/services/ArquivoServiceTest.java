package br.org.apae.atendimento.services;

import br.org.apae.atendimento.dtos.request.ArquivoRequestDTO;
import br.org.apae.atendimento.entities.Arquivo;
import br.org.apae.atendimento.entities.TipoArquivo;
import br.org.apae.atendimento.exceptions.notfound.TipoArquivoNotFoundException;
import br.org.apae.atendimento.mappers.ArquivoMapper;
import br.org.apae.atendimento.repositories.AnexoRepository;
import br.org.apae.atendimento.repositories.TipoArquivoRepository;
import br.org.apae.atendimento.services.storage.ObjectStorageService;
import br.org.apae.atendimento.services.storage.PresignedUrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ArquivoServiceTest {

    @InjectMocks
    private ArquivoService service;

    @Mock
    private AnexoRepository repository;

    @Mock
    private TipoArquivoRepository tipoRepository;

    @Mock
    private ObjectStorageService storageService;

    @Mock
    private PresignedUrlService urlService;

    @Mock
    private ArquivoMapper anexoMapper;

    private UUID profissionalId;
    private UUID pacienteId;
    private ArquivoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        profissionalId = UUID.randomUUID();
        pacienteId = UUID.randomUUID();
        requestDTO = new ArquivoRequestDTO(
                LocalDate.now(),
                1L,
                pacienteId,
                "Título Válido",
                "Descrição Válida"
        );
    }

    @Test
    @DisplayName("Deve lançar exceção ao enviar arquivo vazio")
    void deveLancarExcecaoArquivoVazio() {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", new byte[0]);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            service.salvar(file, requestDTO, profissionalId)
        );
        
        assertEquals("O arquivo enviado está vazio.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao enviar tipo de arquivo não permitido")
    void deveLancarExcecaoTipoNaoPermitido() {
        MockMultipartFile file = new MockMultipartFile("file", "test.exe", "application/x-msdownload", "content".getBytes());
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            service.salvar(file, requestDTO, profissionalId)
        );
        
        assertEquals("Tipo de arquivo não permitido. Apenas PDF e Imagens são aceitos.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao enviar arquivo com extensão forjada (ex: .exe como .pdf)")
    void deveLancarExcecaoMimeTypeIncorreto() {
        // O navegador envia o Content-Type baseado na extensão, mas simulamos aqui um tipo não permitido
        MockMultipartFile file = new MockMultipartFile("file", "malware.pdf", "text/plain", "content".getBytes());
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            service.salvar(file, requestDTO, profissionalId)
        );
        
        assertEquals("Tipo de arquivo não permitido. Apenas PDF e Imagens são aceitos.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve salvar com sucesso quando todos os dados são válidos")
    void deveSalvarComSucesso() {
        MockMultipartFile file = new MockMultipartFile("file", "foto paciente.jpg", "image/jpeg", "content".getBytes());
        TipoArquivo tipoArquivo = new TipoArquivo();
        tipoArquivo.setId(1L);
        
        Arquivo arquivoEntity = new Arquivo();
        arquivoEntity.setTitulo("título válido"); // Canonicalizado
        
        when(tipoRepository.findById(1L)).thenReturn(Optional.of(tipoArquivo));
        when(storageService.uploadArquivo(any(), any())).thenReturn("http://storage/url");
        when(anexoMapper.toEntityPadrao(any())).thenReturn(arquivoEntity);
        when(repository.save(any())).thenReturn(arquivoEntity);
        when(anexoMapper.toDTOPadrao(any())).thenReturn(null);

        assertDoesNotThrow(() -> service.salvar(file, requestDTO, profissionalId));
        
        verify(repository, times(1)).save(any());
        verify(storageService, times(1)).uploadArquivo(any(), any());
    }

    @Test
    @DisplayName("Deve sanitizar o nome do arquivo corretamente")
    void deveSanitizarNomeArquivo() {
        MockMultipartFile file = new MockMultipartFile("file", "Foto do Paciente (João) #2024.jpg", "image/jpeg", "content".getBytes());
        TipoArquivo tipoArquivo = new TipoArquivo();
        tipoArquivo.setId(1L);
        
        when(tipoRepository.findById(1L)).thenReturn(Optional.of(tipoArquivo));
        when(anexoMapper.toEntityPadrao(any())).thenReturn(new Arquivo());
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.salvar(file, requestDTO, profissionalId);

        verify(repository).save(argThat(arquivo -> {
            assertEquals("foto_do_paciente_joao_2024.jpg", arquivo.getNomeArquivo());
            return true;
        }));
    }

    @Test
    @DisplayName("Deve lançar exceção quando TipoArquivo não existe no repositório")
    void deveLancarExcecaoTipoArquivoNaoEncontrado() {
        MockMultipartFile file = new MockMultipartFile("file", "foto.jpg", "image/jpeg", "conteudo".getBytes());

        when(tipoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TipoArquivoNotFoundException.class, () ->
                service.salvar(file, requestDTO, profissionalId)
        );

        verify(storageService, never()).uploadArquivo(any(), any());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve fazer upload quando arquivo é inválido")
    void naoDeveUploadQuandoArquivoInvalido() {
        MockMultipartFile file = new MockMultipartFile("file", "malware.exe", "application/x-msdownload", "conteudo".getBytes());

        assertThrows(IllegalArgumentException.class, () ->
                service.salvar(file, requestDTO, profissionalId)
        );

        verify(storageService, never()).uploadArquivo(any(), any());
    }

    @Test
    @DisplayName("Deve aplicar pipeline completo de sanitização no título e descrição")
    void deveAplicarPipelineNormalizacaoTituloDescricao() {
        MockMultipartFile file = new MockMultipartFile("file", "foto.jpg", "image/jpeg", "conteudo".getBytes());
        ArquivoRequestDTO requestComHtml = new ArquivoRequestDTO(
                LocalDate.now(), 1L, pacienteId,
                "  TÍTULO <script>alert('xss')</script> VÁLIDO  ",
                "  Descrição   válida  "
        );

        TipoArquivo tipoArquivo = new TipoArquivo(1L, "Anexo");
        Arquivo arquivoEntity = new Arquivo();

        when(tipoRepository.findById(1L)).thenReturn(Optional.of(tipoArquivo));
        when(storageService.uploadArquivo(any(), any())).thenReturn("http://url");
        when(anexoMapper.toEntityPadrao(any())).thenReturn(arquivoEntity);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.salvar(file, requestComHtml, profissionalId);

        verify(repository).save(argThat(a -> {
            assertFalse(a.getTitulo().contains("<script>"));
            assertEquals(a.getTitulo(), a.getTitulo().toLowerCase());
            assertFalse(a.getDescricao().startsWith(" "));
            return true;
        }));
    }
}
