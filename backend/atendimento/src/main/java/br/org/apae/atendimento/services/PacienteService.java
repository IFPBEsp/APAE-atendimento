package br.org.apae.atendimento.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import br.org.apae.atendimento.services.storage.ObjectStorageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.org.apae.atendimento.dtos.response.PacienteResponseDTO;
import br.org.apae.atendimento.entities.Arquivo;
import br.org.apae.atendimento.entities.Paciente;
import br.org.apae.atendimento.entities.TipoArquivo;
import br.org.apae.atendimento.exceptions.notfound.PacienteNotFoundException;
import br.org.apae.atendimento.mappers.PacienteMapper;
import br.org.apae.atendimento.repositories.AnexoRepository;
import br.org.apae.atendimento.repositories.PacienteRepository;
import br.org.apae.atendimento.repositories.TipoArquivoRepository;

import org.springframework.web.multipart.MultipartFile;

@Service
public class PacienteService {
    private PacienteRepository repository;
    private PacienteMapper pacienteMapper;
    private ObjectStorageService storageService;

     @Autowired
    private TipoArquivoRepository tipoRepository;

    @Autowired
    private AnexoRepository arquivorepository;

    private static String FOTO_PATH = "/foto/";

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
                .findById(id).orElseThrow(() -> new PacienteNotFoundException());
    }

    public boolean existeRelacao(UUID pacienteId, UUID profissionalId) {
        return repository.existeRelacao(pacienteId, profissionalId);
    }

    public String getNomeCompletoPacienteById(UUID id) {
        return repository.findNomeCompletoById(id);
    }


    public List<PacienteResponseDTO> buscarPaciente(String nome, String cpf, String cidade) {
        return repository.buscarPaciente(nome, cpf, cidade)
                .stream()
                .map(pacienteMapper::toDTOPadrao)
                .collect(Collectors.toList());
    }


    public String adicionarFoto(MultipartFile file, UUID pacienteId){
       String objectName = criarObjectName(pacienteId);
       Paciente paciente = new Paciente();
       paciente.setId(pacienteId);
       
       TipoArquivo tipoArquivo = tipoRepository.getReferenceById(3L);
        Arquivo arquivo = new Arquivo();
        arquivo.setPaciente(paciente);
        arquivo.setObjectName(objectName);
        arquivo.setNomeArquivo(file.getOriginalFilename());
        arquivo.setTipo(tipoArquivo);
        arquivorepository.save(arquivo);

       return storageService.uploadArquivo(objectName, file);
    }


    private String criarObjectName(UUID pacienteId) {
        String objectId = UUID.randomUUID().toString();
        return pacienteId + FOTO_PATH + objectId;
    }
}
