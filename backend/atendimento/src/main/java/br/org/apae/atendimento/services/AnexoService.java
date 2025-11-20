package br.org.apae.atendimento.services;

import br.org.apae.atendimento.entities.Anexo;
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

@Service
public class AnexoService implements ArquivoService<Anexo> {

    @Autowired
    private AnexoRepository repository;

    @Autowired
    private MinioService minioService;

    private static final String ANEXO_PATH = "anexo";

    @Transactional
    public Anexo salvar(UUID pacienteId, Long profissionalId, MultipartFile file, LocalDate date) {
        String objectName = criarObjectName(profissionalId);
        String url = minioService.uploadArquivo(pacienteId.toString(), objectName, file);

        Anexo anexo = new Anexo(
                objectName,
                pacienteId.toString(),
                file.getOriginalFilename(),
                pacienteId,
                profissionalId,
                date,
                url
        );

        repository.save(anexo);
        return anexo;
    }

    @Override
    public String criarObjectName(Long profissionalId) {
        String objectId = UUID.randomUUID().toString();
        return profissionalId + "/" + ANEXO_PATH + "/" + objectId;
    }

    public List<Anexo> listarPorProfissionalEPaciente(Long profissionalId, UUID pacienteId) {
        List<Anexo> anexos = repository.findByProfissionalIdAndPacienteId(profissionalId, pacienteId);

        anexos.forEach(anexo -> {
            String url = minioService.gerarUrlPreAssinada(anexo.getBucket(), anexo.getObjectName());
            anexo.setPresignedUrl(url);
        });

        return anexos;
    }

    @Override
    public List<Anexo> buscarPorData(Long profissionalId, UUID pacienteId, LocalDate data) {
        List<Anexo> anexos = repository.findByProfissionalIdAndPacienteIdAndData(
                profissionalId, pacienteId, data
        );

        anexos.forEach(anexo -> {
            String url = minioService.gerarUrlPreAssinada(anexo.getBucket(), anexo.getObjectName());
            anexo.setPresignedUrl(url);
        });

        return anexos;
    }
}