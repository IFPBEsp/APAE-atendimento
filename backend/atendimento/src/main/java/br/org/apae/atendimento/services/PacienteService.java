package br.org.apae.atendimento.services;

import br.org.apae.atendimento.dtos.response.PacienteDropdownResponseDTO;
import br.org.apae.atendimento.dtos.response.PacienteOptionDTO;
import br.org.apae.atendimento.dtos.response.PacienteResponseDTO;
import br.org.apae.atendimento.entities.views.EnderecoPaciente;
import br.org.apae.atendimento.entities.views.Paciente;
import br.org.apae.atendimento.entities.views.ResponsavelPaciente;
import br.org.apae.atendimento.entities.views.TranstornoPaciente;
import br.org.apae.atendimento.exceptions.notfound.PacienteNotFoundException;
import br.org.apae.atendimento.mappers.PacienteMapper;
import br.org.apae.atendimento.repositories.EnderecoPacienteRepository;
import br.org.apae.atendimento.repositories.PacienteRepository;
import br.org.apae.atendimento.repositories.ResponsavelPacienteRepository;
import br.org.apae.atendimento.repositories.TranstornoPacienteRepository;
import br.org.apae.atendimento.services.storage.ObjectStorageService;
import br.org.apae.atendimento.services.storage.minio.MinioPresignedUrlService;
import br.org.apae.atendimento.services.storage.minio.MinioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class PacienteService {

    private static final String FOTO_PATH = "foto/";

    private final PacienteRepository pacienteRepository;
    private final EnderecoPacienteRepository enderecoRepository;
    private final ResponsavelPacienteRepository responsavelRepository;
    private final TranstornoPacienteRepository transtornoRepository;
    private final PacienteMapper pacienteMapper;
    private final MinioService storageService;

    public PacienteService(PacienteRepository pacienteRepository,
                           EnderecoPacienteRepository enderecoRepository,
                           ResponsavelPacienteRepository responsavelRepository,
                           TranstornoPacienteRepository transtornoRepository,
                           PacienteMapper pacienteMapper,
                           MinioService storageService) {
        this.pacienteRepository  = pacienteRepository;
        this.enderecoRepository  = enderecoRepository;
        this.responsavelRepository = responsavelRepository;
        this.transtornoRepository = transtornoRepository;
        this.pacienteMapper      = pacienteMapper;
        this.storageService      = storageService;
    }

    @Transactional(readOnly = true)
    public PacienteResponseDTO getPaciente(UUID id) {
        Paciente paciente = getPacienteById(id);

        // Busca dados complementares das views separadas
        EnderecoPaciente endereco = enderecoRepository
                .findByPacienteId(id)
                .orElse(null);                          // null-safe no mapper

        List<ResponsavelPaciente> responsaveis = responsavelRepository
                .findAllByPacienteId(id);               // lista vazia se não houver

        List<TranstornoPaciente> transtorno = Collections.singletonList(transtornoRepository
                .findByPacienteId(id)
                .orElse(null));                          // null-safe no mapper

        //String fotoUrl = storageService.uploadArquivo(FOTO_PATH + id, file);

        return pacienteMapper.toDTOCompleto(
                paciente, endereco, responsaveis, transtorno);
    }

    public Paciente getPacienteById(UUID id) {
        return pacienteRepository
                .findById(id)
                .orElseThrow(() -> new PacienteNotFoundException(
                        "Paciente não encontrado no sistema."));
    }

    public boolean existeRelacao(UUID pacienteId, UUID profissionalId) {
        return pacienteRepository.existeRelacao(pacienteId, profissionalId);
    }

    public String getNomeCompletoPacienteById(UUID id) {
        String nome = pacienteRepository.findNomeCompletoById(id);
        if (nome == null) {
            throw new PacienteNotFoundException(
                    "Não foi possível localizar o nome do paciente.");
        }
        return nome;
    }

    @Transactional(readOnly = true)
    public List<PacienteResponseDTO> buscarPaciente(
            UUID profissionalId, String nome, String cpf, String cidade) {

        return pacienteRepository
                .buscarPaciente(profissionalId, nome, cpf, cidade)
                .stream()
                .map(p -> {
                    EnderecoPaciente endereco = enderecoRepository
                            .findByPacienteId(p.getId()).orElse(null);
                    List<ResponsavelPaciente> responsaveis = responsavelRepository
                            .findAllByPacienteId(p.getId());
                    List<TranstornoPaciente> transtorno = Collections.singletonList(transtornoRepository
                            .findByPacienteId(p.getId()).orElse(null));
                    return pacienteMapper.toDTOCompleto(p, endereco, responsaveis, transtorno);
                })
                .toList();
    }

    public String adicionarFoto(MultipartFile file, UUID pacienteId) {
        if (!pacienteRepository.existsById(pacienteId)) {
            throw new PacienteNotFoundException(
                    "Não é possível adicionar foto. Paciente não encontrado.");
        }
        return storageService.uploadArquivo(FOTO_PATH + pacienteId, file);
    }

    @Transactional(readOnly = true)
    public List<PacienteDropdownResponseDTO> listarParaDropdown() {
        return pacienteRepository.listarParaDropdown();
    }

    @Transactional(readOnly = true)
    public List<PacienteOptionDTO> listarOpcoesPorProfissional(UUID profissionalId) {
        return pacienteRepository
                .findByProfissionalId(profissionalId)
                .stream()
                .map(pacienteMapper::toOptionDTO)
                .toList();
    }
}