package br.org.apae.atendimento.services;

import br.org.apae.atendimento.dtos.request.ArquivoRequestDTO;
import br.org.apae.atendimento.dtos.response.ArquivoResponseDTO;
import br.org.apae.atendimento.entities.Arquivo;
import br.org.apae.atendimento.entities.views.ProfissionalSaude;
import br.org.apae.atendimento.entities.TipoArquivo;
import br.org.apae.atendimento.exceptions.invalid.AtendimentoInvalidException;
import br.org.apae.atendimento.exceptions.notfound.ArquivoNotFoundException;
import br.org.apae.atendimento.exceptions.notfound.TipoArquivoNotFoundException;
import br.org.apae.atendimento.mappers.ArquivoMapper;
import br.org.apae.atendimento.repositories.AnexoRepository;
import br.org.apae.atendimento.repositories.ProfissionalSaudeRepository;
import br.org.apae.atendimento.repositories.TipoArquivoRepository;
import br.org.apae.atendimento.services.storage.ObjectStorageService;
import br.org.apae.atendimento.services.storage.PresignedUrlService;
import br.org.apae.atendimento.utils.StringSanitizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ArquivoService {

    @Autowired
    private AnexoRepository repository;

    @Autowired
    private TipoArquivoRepository tipoRepository;

    @Autowired
    private ProfissionalSaudeRepository profissionalRepository;

    @Autowired
    private ObjectStorageService storageService;

    @Autowired
    private PresignedUrlService urlService;

    @Autowired
    private ArquivoMapper anexoMapper;

    private static final List<String> MIME_TYPES_PERMITIDOS = Arrays.asList(
            "application/pdf", "image/jpeg", "image/png", "image/gif"
    );

    private static final String ANEXO_PATH = "anexo";
    private static final String RELATORIO_PATH = "relatorio";

    @Transactional
    public ArquivoResponseDTO salvar(MultipartFile file, ArquivoRequestDTO arquivoRequest, UUID profissionalId) {
        validarIntegridadeArquivo(file);
        validarIntervaloData(arquivoRequest.data());

        TipoArquivo tipoArquivo = tipoRepository.findById(arquivoRequest.tipoArquivo())
                .orElseThrow(() -> new TipoArquivoNotFoundException("O tipo de arquivo selecionado é invalido"));

        String tituloProcessado = StringSanitizer.normalize(arquivoRequest.titulo());
        tituloProcessado = StringSanitizer.sanitize(tituloProcessado);
        tituloProcessado = StringSanitizer.canonicalize(tituloProcessado);

        String descricaoProcessada = StringSanitizer.normalize(arquivoRequest.descricao());
        descricaoProcessada = StringSanitizer.sanitize(descricaoProcessada);

        String nomeSanitizado = StringSanitizer.sanitizeFilename(file.getOriginalFilename());
        String objectName = criarObjectName(arquivoRequest.pacienteId(), profissionalId, arquivoRequest.tipoArquivo());

        String url = storageService.uploadArquivo(objectName, file);

        Arquivo arquivo = anexoMapper.toEntityPadrao(arquivoRequest);
        arquivo.setTitulo(tituloProcessado);
        arquivo.setDescricao(descricaoProcessada);
        arquivo.setObjectName(objectName);
        arquivo.setNomeArquivo(nomeSanitizado);
        arquivo.setTipo(tipoArquivo);

        arquivo.setProfissionalId(profissionalId);

        Arquivo arquivoPersistido = repository.save(arquivo);
        arquivoPersistido.setPresignedUrl(url);

        return anexoMapper.toDTOPadrao(arquivoPersistido);
    }

    public List<ArquivoResponseDTO> listar(UUID profissionalId, UUID pacienteId, Long tipoId) {
        List<Arquivo> arquivos = repository.findByProfissionalIdAndPacienteIdAndTipoId(
                profissionalId, pacienteId, tipoId
        );

        return arquivos.stream()
                .map(anexo -> {
                    String url = urlService.gerarUrlPreAssinada(anexo.getObjectName());
                    anexo.setPresignedUrl(url);
                    return anexoMapper.toDTOPadrao(anexo);
                }).collect(Collectors.toList());
    }

    public List<ArquivoResponseDTO> buscarPorData(UUID profissionalId, UUID pacienteId, Long tipoId, LocalDate data) {
        List<Arquivo> arquivos = repository.findByProfissionalIdAndPacienteIdAndDataAndTipoId(
                profissionalId, pacienteId, data, tipoId
        );

        return arquivos.stream()
                .map(anexo -> {
                    String url = urlService.gerarUrlPreAssinada(anexo.getObjectName());
                    anexo.setPresignedUrl(url);
                    return anexoMapper.toDTOPadrao(anexo);
                }).collect(Collectors.toList());
    }

    public void deletar(String objectName) {
        if (!repository.existsById(objectName)) {
            throw new ArquivoNotFoundException("O arquivo selecionado não existe ou já foi apagado.");
        }

        repository.deleteById(objectName);
        storageService.deletarArquivo(objectName);
    }

    private void validarIntegridadeArquivo(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AtendimentoInvalidException("O arquivo enviado está vazio.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !MIME_TYPES_PERMITIDOS.contains(contentType)) {
            throw new AtendimentoInvalidException("Tipo de arquivo não permitido. Apenas PDF e Imagens são aceitos.");
        }
    }

    private void validarIntervaloData(LocalDate data) {
        LocalDate hoje = LocalDate.now();
        LocalDate minimo = hoje.minusYears(30);
        if (data.isBefore(minimo) || data.isAfter(hoje)) {
            throw new AtendimentoInvalidException("Data fora do intervalo permitido (Fundação APAE - 30 anos até hoje).");
        }
    }

    private String criarObjectName(UUID pacienteId, UUID profissionalId, Long tipoArquivoId) {
        String objectId = UUID.randomUUID().toString();
        String path = (tipoArquivoId == 1L) ? ANEXO_PATH : RELATORIO_PATH;
        return pacienteId + "/" + profissionalId + "/" + path + "/" + objectId;
    }
}