package br.org.apae.atendimento.services;

import br.org.apae.atendimento.dtos.request.ArquivoRequestDTO;
import br.org.apae.atendimento.dtos.response.ArquivoResponseDTO;
import br.org.apae.atendimento.entities.Arquivo;
import br.org.apae.atendimento.entities.TipoArquivo;
import br.org.apae.atendimento.mappers.ArquivoMapper;
import br.org.apae.atendimento.repositories.AnexoRepository;
import br.org.apae.atendimento.repositories.TipoArquivoRepository;
import br.org.apae.atendimento.services.storage.ObjectStorageService;
import br.org.apae.atendimento.services.storage.PresignedUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
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

    private static final String ANEXO_PATH = "anexo";
    private static final String RELATORIO_PATH = "relatorio";

    @Transactional
    public ArquivoResponseDTO salvar(MultipartFile file, ArquivoRequestDTO arquivoRequest, UUID profissionalId) {
        String objectName = criarObjectName(arquivoRequest.pacienteId(), profissionalId,
                arquivoRequest.tipoArquivo());

        String url = storageService.uploadArquivo(objectName, file);

        TipoArquivo tipoArquivo = tipoRepository.getReferenceById(arquivoRequest.tipoArquivo());

        Arquivo arquivo = anexoMapper.toEntityPadrao(arquivoRequest);
        arquivo.setObjectName(objectName);
        arquivo.setNomeArquivo(file.getOriginalFilename());
        arquivo.setTipo(tipoArquivo);

        Arquivo arquivoPersistido = repository.save(arquivo);
        arquivoPersistido.setPresignedUrl(url);
        return anexoMapper.toDTOPadrao(arquivoPersistido);
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
        repository.deleteById(objectName);
        storageService.deletarArquivo(objectName);
    }
}