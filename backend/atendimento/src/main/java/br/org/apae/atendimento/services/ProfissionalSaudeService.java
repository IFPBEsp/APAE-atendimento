package br.org.apae.atendimento.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import br.org.apae.atendimento.exceptions.ProfissionalSaudeNotFoundException;
import br.org.apae.atendimento.mappers.PacienteMapper;
import br.org.apae.atendimento.mappers.ProfissionalMapper;

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

    public ProfissionalSaudeService(ProfissionalSaudeRepository profissionalSaudeRepository,
                                    ProfissionalMapper profissionalMapper,
                                    PacienteMapper pacienteMapper) {
        this.repository = profissionalSaudeRepository;
        this.pacienteMapper = pacienteMapper;
        this.profissionalMapper = profissionalMapper;
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

        return pacientes
                .stream()
                .map(paciente -> pacienteMapper.toDTOPadrao(paciente))
                .collect(Collectors.toList());
    }


    public String getPrimeiroNome (UUID id) {
        String nome = repository.findPrimeiroNomeById(id);
        if(nome == null) {
            throw new ProfissionalSaudeNotFoundException();
        }
        return nome;
    }
}
