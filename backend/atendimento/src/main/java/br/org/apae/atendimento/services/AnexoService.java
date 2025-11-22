package br.org.apae.atendimento.services;

import br.org.apae.atendimento.dtos.ArquivoDTO;
import br.org.apae.atendimento.entities.Anexo;
import br.org.apae.atendimento.mappers.AnexoMapper;
import br.org.apae.atendimento.repositories.AnexoRepository;
import br.org.apae.atendimento.services.interfaces.ArquivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AnexoService implements ArquivoService<ArquivoDTO> {

    @Autowired
    private AnexoRepository repository;

    @Autowired
    private MinioService minioService;

    @Autowired
    private PresignedUrlService urlService;

    private static final String ANEXO_PATH = "anexo";

    private static AnexoMapper anexoMapper;

    @Transactional
    public ArquivoDTO salvar(MultipartFile file, ArquivoDTO metadata) {
        String objectName = criarObjectName(metadata.profissionalId());
        String url = minioService.uploadArquivo(metadata.pacienteId().toString(), metadata.objectName(), file);
        Anexo anexo = anexoMapper.toEntityPadrao(metadata);
        anexo.setObjectName(objectName);
        anexo.setPresignedUrl(url);
        anexo.setNomeAnexo(file.getOriginalFilename());
        Anexo anexoPersistido = repository.save(anexo);

        return anexoMapper.toDTOPadrao(anexoPersistido);
    }

    @Override
    public String criarObjectName(Long profissionalId) {
        String objectId = UUID.randomUUID().toString();
        return profissionalId + "/" + ANEXO_PATH + "/" + objectId;
    }

    public List<ArquivoDTO> listarPorProfissionalEPaciente(Long profissionalId, UUID pacienteId) {
        List<Anexo> anexos = repository.findByProfissionalIdAndPacienteId(profissionalId, pacienteId);
        return  anexos.stream()
                .map(anexo -> {
                    String url = urlService.gerarUrlPreAssinada(anexo.getBucket(), anexo.getObjectName());
                    anexo.setPresignedUrl(url);
                    return anexoMapper.toDTOPadrao(anexo);
                }).collect(Collectors.toList());
    }

    @Override
    public List<ArquivoDTO> buscarPorData(Long profissionalId, UUID pacienteId, LocalDate data) {
        List<Anexo> anexos = repository.findByProfissionalIdAndPacienteIdAndData(
                profissionalId, pacienteId, data
        );
        return anexos.stream()
                .map(anexo -> {
                    String url = urlService.gerarUrlPreAssinada(anexo.getBucket(), anexo.getObjectName());
                    anexo.setPresignedUrl(url);
                    return anexoMapper.toDTOPadrao(anexo);
                }).collect(Collectors.toList());
    }

    @Override
    public void deletar(String bucket, String objectName) {
        repository.deleteById(objectName);
        minioService.deletarArquivo(bucket, objectName);
    }
}