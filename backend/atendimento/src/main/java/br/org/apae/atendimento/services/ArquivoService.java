package br.org.apae.atendimento.services;

import br.org.apae.atendimento.dtos.request.ArquivoRequestDTO;
import br.org.apae.atendimento.dtos.response.ArquivoResponseDTO;
import br.org.apae.atendimento.entities.Arquivo;
import br.org.apae.atendimento.entities.ProfissionalSaude;
import br.org.apae.atendimento.entities.TipoArquivo;
import br.org.apae.atendimento.exceptions.notfound.ArquivoNotFoundException;
import br.org.apae.atendimento.exceptions.notfound.TipoArquivoNotFoundException;
import br.org.apae.atendimento.mappers.ArquivoMapper;
import br.org.apae.atendimento.repositories.AnexoRepository;
import br.org.apae.atendimento.repositories.TipoArquivoRepository;
import br.org.apae.atendimento.services.storage.ObjectStorageService;
import br.org.apae.atendimento.services.storage.PresignedUrlService;
import br.org.apae.atendimento.utils.StringHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.Normalizer;
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

        TipoArquivo tipoArquivo = tipoRepository.findById(arquivoRequest.tipoArquivo())
                .orElseThrow(() -> new TipoArquivoNotFoundException("O tipo de arquivo selecionado é invalido"));

        String nomeSanitizado = sanitizarNomeArquivo(file.getOriginalFilename());

        // Pipeline de Normalização, Sanitização e Canonicalização
        String tituloProcessado = StringHandler.normalizar(arquivoRequest.titulo());
        tituloProcessado = StringHandler.sanitizar(tituloProcessado);
        tituloProcessado = StringHandler.canonicalizar(tituloProcessado);

        String descricaoProcessada = StringHandler.normalizar(arquivoRequest.descricao());
        descricaoProcessada = StringHandler.sanitizar(descricaoProcessada);

        String objectName = criarObjectName(arquivoRequest.pacienteId(), profissionalId,
                arquivoRequest.tipoArquivo());

        String url = storageService.uploadArquivo(objectName, file);

        Arquivo arquivo = anexoMapper.toEntityPadrao(arquivoRequest);
        arquivo.setTitulo(tituloProcessado);
        arquivo.setDescricao(descricaoProcessada);
        arquivo.setObjectName(objectName);
        arquivo.setNomeArquivo(nomeSanitizado);
        arquivo.setTipo(tipoArquivo);

        ProfissionalSaude profissionalSaude = new ProfissionalSaude(profissionalId);
        arquivo.setProfissional(profissionalSaude);

        Arquivo arquivoPersistido = repository.save(arquivo);
        arquivoPersistido.setPresignedUrl(url);

        return anexoMapper.toDTOPadrao(arquivoPersistido);
    }

    private void validarIntegridadeArquivo(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("O arquivo enviado está vazio.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !MIME_TYPES_PERMITIDOS.contains(contentType)) {
            throw new IllegalArgumentException("Tipo de arquivo não permitido. Apenas PDF e Imagens são aceitos.");
        }
    }

    private String sanitizarNomeArquivo(String originalFilename) {
        if (originalFilename == null) return "arquivo_sem_nome";

        String normalized = Normalizer.normalize(originalFilename, Normalizer.Form.NFD);
        String result = normalized.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

        result = result.replaceAll("[^a-zA-Z0-9.\\-_]", "_");

        result = result.replaceAll("_{2,}", "_");

        return result.toLowerCase();
    }

    private String criarObjectName(UUID pacienteId, UUID profissionalId, Long tipoArquivoId) {
        String objectId = UUID.randomUUID().toString();
        if (tipoArquivoId == 1L){
            return pacienteId + "/" + profissionalId + "/" + ANEXO_PATH + "/" + objectId;
        } else {
            return pacienteId + "/" + profissionalId + "/" + RELATORIO_PATH + "/" + objectId;
        }
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
}