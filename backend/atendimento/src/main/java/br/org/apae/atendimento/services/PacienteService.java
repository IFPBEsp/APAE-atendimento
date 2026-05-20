package br.org.apae.atendimento.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import br.org.apae.atendimento.dtos.response.PacienteDropdownResponseDTO;
import br.org.apae.atendimento.dtos.response.PaginatedResponseDTO;
import br.org.apae.atendimento.dtos.response.PaginationMetaDTO;
import br.org.apae.atendimento.services.storage.ObjectStorageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.org.apae.atendimento.dtos.response.PacienteResponseDTO;
import br.org.apae.atendimento.entities.Paciente;
import br.org.apae.atendimento.exceptions.notfound.PacienteNotFoundException;
import br.org.apae.atendimento.mappers.PacienteMapper;
import br.org.apae.atendimento.repositories.PacienteRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PacienteService {
    private PacienteRepository repository;
    private PacienteMapper pacienteMapper;
    private ObjectStorageService storageService;

    private static String FOTO_PATH = "foto/";

    public PacienteService(PacienteRepository pacienteRepository, PacienteMapper pacienteMapper, ObjectStorageService storageService) {
        this.repository = pacienteRepository;
        this.pacienteMapper = pacienteMapper;
        this.storageService = storageService;
    }

    public PacienteResponseDTO getPaciente(UUID id) {
        Paciente paciente = getPacienteById(id);
        return this.pacienteMapper.toDTOPadrao(paciente);
    }

    public Paciente getPacienteById(UUID id) {
        return repository
                .findById(id).orElseThrow(() -> new PacienteNotFoundException("Paciente não encontrado no sistema."));
    }

    public boolean existeRelacao(UUID pacienteId, UUID profissionalId) {
        return repository.existeRelacao(pacienteId, profissionalId);
    }

    public String getNomeCompletoPacienteById(UUID id) {
        String nome = repository.findNomeCompletoById(id);
        if (nome == null) {
            throw new PacienteNotFoundException("Não foi possível localizar o nome do paciente.");
        }
        return nome;
    }


    public PaginatedResponseDTO<PacienteResponseDTO> buscarPaciente(UUID profissionalId, String nome, String cpf, String cidade, int page, int limit) {
        Pageable pageable = PageRequest.of(page -1, limit);

        Page<Paciente> pacientePage = repository.buscarPaciente(profissionalId, nome, cpf, cidade, pageable);

        List<PacienteResponseDTO> data = pacientePage.getContent()
                .stream()
                .map(pacienteMapper::toDTOPadrao)
                .collect(Collectors.toList());

        PaginationMetaDTO meta = new PaginationMetaDTO(
                page,
                limit,
                pacientePage.getTotalElements(),
                pacientePage.getTotalPages(),
                pacientePage.hasNext(),
                pacientePage.hasPrevious()
        );

        return new PaginatedResponseDTO<>(data, meta);
    }

    public String adicionarFoto(MultipartFile file, UUID pacienteId){

        if (!repository.existsById(pacienteId)) {
            throw new PacienteNotFoundException("Não é possivel adicionar foto. Paciente não encontrado.");
        }
       return storageService.uploadArquivo(FOTO_PATH + pacienteId, file);
    }

    @Transactional(readOnly = true)
    public List<PacienteDropdownResponseDTO> listarParaDropdown() {
        return repository.listarParaDropdown();
    }
}
