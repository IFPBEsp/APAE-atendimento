package br.org.apae.atendimento.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import br.org.apae.atendimento.dtos.response.PacienteOptionDTO;
import br.org.apae.atendimento.exceptions.notfound.ProfissionalSaudeNotFoundException;
import br.org.apae.atendimento.mappers.PacienteMapper;
import br.org.apae.atendimento.mappers.ProfissionalMapper;

import br.org.apae.atendimento.repositories.PacienteRepository;
import br.org.apae.atendimento.services.storage.ObjectStorageService;
import br.org.apae.atendimento.services.storage.PresignedUrlService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.org.apae.atendimento.dtos.response.PacienteResponseDTO;
import br.org.apae.atendimento.dtos.response.ProfissionalResponseDTO;
import br.org.apae.atendimento.entities.Paciente;
import br.org.apae.atendimento.entities.ProfissionalSaude;
import br.org.apae.atendimento.repositories.ProfissionalSaudeRepository;

@Service
public class ProfissionalSaudeService {

    private final ProfissionalSaudeRepository repository;
    private final ProfissionalMapper profissionalMapper;
    private final PacienteMapper pacienteMapper;
    private final PacienteRepository pacienteRepository;
    private final PresignedUrlService urlService;

    private static String FOTO_PATH = "foto/";

    public ProfissionalSaudeService(ProfissionalSaudeRepository profissionalSaudeRepository,
                                    ProfissionalMapper profissionalMapper,
                                    PacienteMapper pacienteMapper,
                                    PacienteRepository pacienteRepository,
                                    PresignedUrlService urlService) {
        this.repository = profissionalSaudeRepository;
        this.pacienteMapper = pacienteMapper;
        this.profissionalMapper = profissionalMapper;
        this.pacienteRepository = pacienteRepository;
        this.urlService = urlService;
    }

    public ProfissionalResponseDTO getProfissionalByIdDTO(UUID id){
        ProfissionalSaude profissionalSaude = repository.findById(id)
                .orElseThrow(() -> new ProfissionalSaudeNotFoundException());

        return profissionalMapper.toDTOPadrao(profissionalSaude);
    }

    public ProfissionalSaude getProfissionalById(UUID id){
        ProfissionalSaude profissionalSaude = repository.findById(id)
                .orElseThrow(() -> new ProfissionalSaudeNotFoundException());

        return profissionalSaude;
    }

    public List<PacienteResponseDTO> getPacientesDoProfissional (UUID id) {
        ProfissionalSaude profissionalSaude = repository.findById(id)
                .orElseThrow(() -> new ProfissionalSaudeNotFoundException());
        List<Paciente> pacientes = profissionalSaude.getPacientes();

        return pacientes.stream()
                .map(paciente -> {
                    String url = urlService.gerarUrlPreAssinada(FOTO_PATH + paciente.getId());
                    paciente.setFotoPreAssinada(url);
                    return pacienteMapper.toDTOPadrao(paciente);
                }).collect(Collectors.toList());
    }

    public List<PacienteOptionDTO> getPacienteOption(UUID profissionalId){
        List<Paciente> pacientes = pacienteRepository.findByProfissionais_Id(profissionalId);
        return pacientes.stream()
                .map(paciente -> pacienteMapper.toOptionDTO(paciente))
                .collect(Collectors.toList());
    }


    public String getPrimeiroNome (UUID id) {
        String nome = repository.findPrimeiroNomeById(id);
        if(nome == null) {
            throw new ProfissionalSaudeNotFoundException();
        }
        return nome;
    }

    private String getEmailUsuarioLogado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new AccessDeniedException("Usuário não autenticado");
        }
        return auth.getName();
    }
    public ProfissionalResponseDTO getProfissionalLogado() {
        String email = getEmailUsuarioLogado();

        ProfissionalSaude profissional = repository
                .findByEmail(email)
                .orElseThrow(() ->
                        new AccessDeniedException("Usuário não autorizado para o sistema")
                );

        return profissionalMapper.toDTOPadrao(profissional);
    }

    public boolean existByEmail(String email){
        return repository.existsByEmail(email);
    }

}
