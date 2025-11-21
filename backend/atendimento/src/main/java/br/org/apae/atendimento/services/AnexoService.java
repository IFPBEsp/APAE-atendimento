package br.org.apae.atendimento.services;

import br.org.apae.atendimento.entities.Arquivo;
import br.org.apae.atendimento.entities.Paciente;
import br.org.apae.atendimento.entities.ProfissionalSaude;
import br.org.apae.atendimento.entities.TipoArquivo;
import br.org.apae.atendimento.repositories.AnexoRepository;
import br.org.apae.atendimento.repositories.PacienteRepository;
import br.org.apae.atendimento.repositories.ProfissionalSaudeRepository;
import br.org.apae.atendimento.repositories.TipoArquivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class AnexoService{

    @Autowired
    private AnexoRepository repository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private ProfissionalSaudeRepository profissionalSaudeRepository;

    @Autowired
    private TipoArquivoRepository tipoRepository;

    @Autowired
    private MinioService minioService;

    @Autowired
    private PresignedUrlService urlService;

    private static final String ANEXO_PATH = "anexo";

    @Transactional
    public Arquivo salvar(UUID pacienteId, UUID profissionalId, MultipartFile file, LocalDate date, Long tipoId) {
        String objectName = criarObjectName(profissionalId);
        String url = minioService.uploadArquivo(pacienteId.toString(), objectName, file);

        Paciente paciente = pacienteRepository.getReferenceById(pacienteId);
        ProfissionalSaude profissionalSaude = profissionalSaudeRepository.getReferenceById(profissionalId);
        TipoArquivo tipoArquivo = tipoRepository.getReferenceById(tipoId);

        Arquivo anexo = new Arquivo(
                objectName,
                pacienteId.toString(),
                file.getOriginalFilename(),
                paciente,
                profissionalSaude,
                date,
                tipoArquivo,
                url
        );

        repository.save(anexo);
        return anexo;
    }

    public String criarObjectName(UUID profissionalId) {
        String objectId = UUID.randomUUID().toString();
        return profissionalId + "/" + ANEXO_PATH + "/" + objectId;
    }

    public List<Arquivo> listar(UUID profissionalId, UUID pacienteId, Long tipoId) {
        List<Arquivo> anexos = repository.findByProfissionalIdAndPacienteIdAndTipoId(
                profissionalId, pacienteId, tipoId
        );

        anexos.forEach(anexo -> {
            String url = urlService.gerarUrlPreAssinada(anexo.getBucket(), anexo.getObjectName());
            anexo.setPresignedUrl(url);
        });

        return anexos;
    }

    public List<Arquivo> buscarPorData(UUID profissionalId, UUID pacienteId, LocalDate data, Long tipoId) {
        List<Arquivo> anexos = repository.findByProfissionalIdAndPacienteIdAndDataAndTipoId(
                profissionalId, pacienteId, data, tipoId
        );

        anexos.forEach(anexo -> {
            String url = urlService.gerarUrlPreAssinada(anexo.getBucket(), anexo.getObjectName());
            anexo.setPresignedUrl(url);
        });

        return anexos;
    }

    public void deletar(String bucket, String objectName) {
        repository.deleteById(objectName);
        minioService.deletarArquivo(bucket, objectName);
    }
}