package br.org.apae.atendimento.services;

import java.util.List;
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

    private final ProfissionalSaudeRepository profissionalSaudeRepository;
    private final ProfissionalMapper profissionalMapper;
    private final PacienteMapper pacienteMapper;

    public ProfissionalSaudeService(ProfissionalSaudeRepository profissionalSaudeRepository,
                                    ProfissionalMapper profissionalMapper,
                                    PacienteMapper pacienteMapper) {
        this.profissionalSaudeRepository = profissionalSaudeRepository;
        this.pacienteMapper = pacienteMapper;
        this.profissionalMapper = profissionalMapper;
    }

    public ProfissionalSaude getProfissionalById(Long id) {
        return profissionalSaudeRepository.findById(id).orElseThrow(() -> new ProfissionalSaudeNotFoundException());
    }

    public ProfissionalResponseDTO getProfissionalByIdDTO(Long id){
        ProfissionalSaude profissionalSaude = getProfissionalById(id);
        return profissionalMapper.toDTOPadrao(profissionalSaude);
    }

    public List<PacienteResponseDTO> getPacientesDoProfissional (Long id) {
        ProfissionalSaude profissionalSaude = getProfissionalById(id);
        List<Paciente> pacientes = profissionalSaude.getPacientes();
        return pacientes
                .stream()
                .map(paciente -> pacienteMapper.toDTOPadrao(paciente))
                .collect(Collectors.toList());
    }


    public String getPrimeiroNome (Long id) {
        String nome = profissionalSaudeRepository.findPrimeiroNomeById(id);
        if(nome == null) {
            throw new ProfissionalSaudeNotFoundException();
        }
        return nome;
    }
}
