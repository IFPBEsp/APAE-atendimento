package br.org.apae.atendimento.services;

import br.org.apae.atendimento.dtos.request.ArquivoRequestDTO;
import br.org.apae.atendimento.entities.Arquivo;
import br.org.apae.atendimento.entities.TipoArquivo;
import br.org.apae.atendimento.exceptions.invalid.AtendimentoInvalidException;
import br.org.apae.atendimento.exceptions.invalid.RelacaoInvalidException;
import br.org.apae.atendimento.exceptions.notfound.TipoArquivoNotFoundException;
import br.org.apae.atendimento.mappers.ArquivoMapper;
import br.org.apae.atendimento.repositories.AnexoRepository;
import br.org.apae.atendimento.repositories.TipoArquivoRepository;
import br.org.apae.atendimento.services.storage.ObjectStorageService;
import br.org.apae.atendimento.services.storage.PresignedUrlService;
import br.org.apae.atendimento.dtos.response.PaginatedResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArquivoServiceTest {

    @InjectMocks
    private ArquivoService service;

    @Mock private AnexoRepository repository;
    @Mock private TipoArquivoRepository tipoRepository;
    @Mock private PacienteService pacienteService;
    @Mock private ObjectStorageService storageService;
    @Mock private PresignedUrlService urlService;
    @Mock private ArquivoMapper anexoMapper;

    private UUID profissionalId;
    private UUID pacienteId;
    private ArquivoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        profissionalId = UUID.randomUUID();
        pacienteId = UUID.randomUUID();
        requestDTO = new ArquivoRequestDTO(
                LocalDate.now(), 1L, pacienteId,
                "Título Válido", "Descrição Válida"
        );

        TipoArquivo tipoArquivo = new TipoArquivo();
        tipoArquivo.setId(1L);
        tipoArquivo.setTipo("PDF");
        lenient().when(tipoRepository.findById(1L)).thenReturn(Optional.of(tipoArquivo));
        lenient().when(pacienteService.existeRelacao(pacienteId, profissionalId)).thenReturn(true);
    }

    @Test
    @DisplayName("Deve lançar exceção ao enviar arquivo vazio")
    void deveLancarExcecaoArquivoVazio() {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", new byte[0]);
        AtendimentoInvalidException ex = assertThrows(AtendimentoInvalidException.class,
                () -> service.salvar(file, requestDTO, profissionalId));
        assertEquals("O arquivo enviado está vazio.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao enviar tipo de arquivo não permitido")
    void deveLancarExcecaoTipoNaoPermitido() {
        MockMultipartFile file = new MockMultipartFile("file", "test.exe", "application/x-msdownload", "conteudo".getBytes());
        AtendimentoInvalidException ex = assertThrows(AtendimentoInvalidException.class,
                () -> service.salvar(file, requestDTO, profissionalId));
        assertEquals("Tipo de arquivo não permitido. Apenas PDF e Imagens são aceitos.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao enviar arquivo com MIME incorreto")
    void deveLancarExcecaoMimeTypeIncorreto() {
        MockMultipartFile file = new MockMultipartFile("file", "malware.pdf", "text/plain", "conteudo".getBytes());
        AtendimentoInvalidException ex = assertThrows(AtendimentoInvalidException.class,
                () -> service.salvar(file, requestDTO, profissionalId));
        assertEquals("Tipo de arquivo não permitido. Apenas PDF e Imagens são aceitos.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve salvar com sucesso quando todos os dados são válidos")
    void deveSalvarComSucesso() {
        MockMultipartFile file = new MockMultipartFile("file", "foto paciente.jpg", "image/jpeg", "conteudo".getBytes());
        Arquivo arquivoEntity = new Arquivo();
        arquivoEntity.setTitulo("título válido");

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
        MockMultipartFile file = new MockMultipartFile("file", "Foto do Paciente (João) #2024.jpg", "image/jpeg", "conteudo".getBytes());
        when(anexoMapper.toEntityPadrao(any())).thenReturn(new Arquivo());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(storageService.uploadArquivo(any(), any())).thenReturn("http://url");

        service.salvar(file, requestDTO, profissionalId);

        verify(repository).save(argThat(arq -> {
            assertEquals("foto_do_paciente__joão___2024.jpg", arq.getNomeArquivo().toLowerCase());
            return true;
        }));
    }

    @Test
    @DisplayName("Deve lançar exceção quando TipoArquivo não existe")
    void deveLancarExcecaoTipoArquivoNaoEncontrado() {
        MockMultipartFile file = new MockMultipartFile("file", "foto.jpg", "image/jpeg", "conteudo".getBytes());
        when(tipoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TipoArquivoNotFoundException.class,
                () -> service.salvar(file, requestDTO, profissionalId));

        verify(storageService, never()).uploadArquivo(any(), any());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve fazer upload quando arquivo é inválido")
    void naoDeveUploadQuandoArquivoInvalido() {
        MockMultipartFile file = new MockMultipartFile("file", "malware.exe", "application/x-msdownload", "conteudo".getBytes());
        assertThrows(AtendimentoInvalidException.class,
                () -> service.salvar(file, requestDTO, profissionalId));
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

        Arquivo arquivoEntity = new Arquivo();

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

    @Test
    @DisplayName("Deve listar arquivos paginados com metadados corretos")
    void deveListarArquivosPaginados() {
        Long tipoId = 1L;
        Arquivo arquivo = new Arquivo();
        arquivo.setObjectName("obj-1");

        Pageable pageableEsperado = PageRequest.of(0, 10);
        Page<Arquivo> paginaMock = new PageImpl<>(List.of(arquivo), pageableEsperado, 25);

        when(repository.findByProfissionalIdAndPacienteIdAndTipoId(profissionalId, pacienteId, tipoId, pageableEsperado))
                .thenReturn(paginaMock);
        when(urlService.gerarUrlPreAssinada(any())).thenReturn("http://url");
        when(anexoMapper.toDTOPadrao(any())).thenReturn(null);

        PaginatedResponseDTO<?> resultado = service.listar(profissionalId, pacienteId, tipoId, 1, 10);

        assertEquals(1, resultado.data().size());
        assertEquals(1, resultado.paginationMetaDTO().page());
        assertEquals(10, resultado.paginationMetaDTO().limit());
        assertEquals(25, resultado.paginationMetaDTO().totalItems());
        assertEquals(3, resultado.paginationMetaDTO().totalPages());
        assertTrue(resultado.paginationMetaDTO().hasNextPage());
        assertFalse(resultado.paginationMetaDTO().hasPreviousPage());
    }

    @Test
    @DisplayName("Deve converter página 1 para índice 0 do Spring Data ao listar arquivos")
    void deveConverterPaginaParaIndiceZero() {
        Long tipoId = 1L;
        when(repository.findByProfissionalIdAndPacienteIdAndTipoId(eq(profissionalId), eq(pacienteId), eq(tipoId), argThat(p -> p.getPageNumber() == 2 && p.getPageSize() == 5)))
                .thenReturn(new PageImpl<>(List.of()));

        service.listar(profissionalId, pacienteId, tipoId, 3, 5);

        verify(repository).findByProfissionalIdAndPacienteIdAndTipoId(eq(profissionalId), eq(pacienteId), eq(tipoId), argThat(p -> p.getPageNumber() == 2 && p.getPageSize() == 5));
    }

    @Test
    @DisplayName("Deve lançar exceção ao listar quando profissional não possui vínculo com paciente")
    void deveLancarExcecaoAoListarSemRelacaoPacienteProfissional() {
        when(pacienteService.existeRelacao(pacienteId, profissionalId)).thenReturn(false);

        assertThrows(RelacaoInvalidException.class,
                () -> service.listar(profissionalId, pacienteId, 1L, 1, 10));

        verify(repository, never()).findByProfissionalIdAndPacienteIdAndTipoId(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando profissional não possui vínculo com paciente")
    void deveLancarExcecaoQuandoSemRelacaoPacienteProfissional() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "foto.jpg",
                "image/jpeg",
                "conteudo".getBytes()
        );

        when(pacienteService.existeRelacao(pacienteId, profissionalId)).thenReturn(false);

        assertThrows(RelacaoInvalidException.class,
                () -> service.salvar(file, requestDTO, profissionalId));

        verify(storageService, never()).uploadArquivo(any(), any());
        verify(repository, never()).save(any());
    }
}