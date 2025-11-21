package br.org.apae.atendimento.services;

import java.util.List;
import java.util.UUID;

import br.org.apae.atendimento.exceptions.ProfissionalSaudeNotFoundException;
import org.springframework.stereotype.Service;

import br.org.apae.atendimento.entities.Paciente;
import br.org.apae.atendimento.entities.ProfissionalSaude;
import br.org.apae.atendimento.repositories.ProfissionalSaudeRepository;

@Service
public class ProfissionalSaudeService {

    private final ProfissionalSaudeRepository profissionalSaudeRepository;

    public ProfissionalSaudeService(ProfissionalSaudeRepository profissionalSaudeRepository) {
        this.profissionalSaudeRepository = profissionalSaudeRepository;
    }

    public ProfissionalSaude getProfissionalById(UUID id) {
        return profissionalSaudeRepository.findById(id).orElseThrow(() -> new ProfissionalSaudeNotFoundException());
    }

    public List<Paciente> getPacientesDoProfissional (UUID id) {
        ProfissionalSaude profissionalSaude = getProfissionalById(id);
        return profissionalSaude.getPacientes();
    }

    public String getPrimeiroNome (UUID id) {
        String nome = profissionalSaudeRepository.findPrimeiroNomeById(id);
        if(nome == null) {
            throw new ProfissionalSaudeNotFoundException();
        }
        return nome;
    }
}
