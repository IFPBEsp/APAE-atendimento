package br.org.apae.atendimento.services;

import br.org.apae.atendimento.dtos.request.ArquivoRequestDTO;
import br.org.apae.atendimento.dtos.response.ArquivoResponseDTO;
import br.org.apae.atendimento.entities.Arquivo;
import br.org.apae.atendimento.entities.TipoArquivo;
import br.org.apae.atendimento.mappers.AnexoMapper;
import br.org.apae.atendimento.repositories.AnexoRepository;
import br.org.apae.atendimento.repositories.TipoArquivoRepository;
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
    private MinioService minioService;

    @Autowired
    private PresignedUrlService urlService;

    @Autowired
    private AnexoMapper anexoMapper;

    private static final String ANEXO_PATH = "anexo";
    private static final String RELATORIO_PATH = "relatorio";

    @Transactional
    public ArquivoResponseDTO salvar(MultipartFile file, ArquivoRequestDTO arquivoRequest) {
        String objectName = criarObjectName(arquivoRequest.profissionalId(), arquivoRequest.tipoArquivo());
        String url = minioService.uploadArquivo(arquivoRequest.pacienteId().toString(), objectName, file);

        TipoArquivo tipoArquivo = tipoRepository.getReferenceById(arquivoRequest.tipoArquivo());

        Arquivo arquivo = anexoMapper.toEntityPadrao(arquivoRequest);
        arquivo.setObjectName(objectName);
        arquivo.setNomeArquivo(file.getOriginalFilename());
        arquivo.setPresignedUrl(url);
        arquivo.setTipo(tipoArquivo);

        Arquivo arquivoPersistido = repository.save(arquivo);
        arquivoPersistido.setPresignedUrl(url);
        return anexoMapper.toDTOPadrao(arquivoPersistido);
    }

    private String criarObjectName(UUID profissionalId, Long tipoArquivoId) {
        String objectId = UUID.randomUUID().toString();
        if (tipoArquivoId == 1L){
            return profissionalId + "/" + ANEXO_PATH + "/" + objectId;
        } else {
            return profissionalId + "/" + RELATORIO_PATH + "/" + objectId;
        }
    }


    public List<ArquivoResponseDTO> listar(ArquivoRequestDTO arquivoRequest) {
        List<Arquivo> arquivos = repository.findByProfissionalIdAndPacienteIdAndTipoId(
                arquivoRequest.profissionalId(), arquivoRequest.pacienteId(), arquivoRequest.tipoArquivo()
        );

        return arquivos.stream()
                .map(anexo -> {
                    String url = urlService.gerarUrlPreAssinada(
                            arquivoRequest.profissionalId().toString(), anexo.getObjectName());

                    anexo.setPresignedUrl(url);
                    return anexoMapper.toDTOPadrao(anexo);
                }).collect(Collectors.toList());
    }

    public List<ArquivoResponseDTO> buscarPorData(ArquivoRequestDTO arquivoRequest, LocalDate data) {
        List<Arquivo> arquivos = repository.findByProfissionalIdAndPacienteIdAndDataAndTipoId(
                arquivoRequest.profissionalId(), arquivoRequest.pacienteId(), data, arquivoRequest.tipoArquivo()
        );

        return arquivos.stream()
                .map(anexo -> {
                    String url = urlService.gerarUrlPreAssinada(
                            arquivoRequest.pacienteId().toString(), anexo.getObjectName());
                    anexo.setPresignedUrl(url);
                    return anexoMapper.toDTOPadrao(anexo);
                }).collect(Collectors.toList());
    }

    public void deletar(String bucket, String objectName) {
        repository.deleteById(objectName);
        minioService.deletarArquivo(bucket, objectName);
    }
}