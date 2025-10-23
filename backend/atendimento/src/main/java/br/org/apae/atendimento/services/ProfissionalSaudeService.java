package br.org.apae.atendimento.services;

import br.org.apae.atendimento.entities.Paciente;
import br.org.apae.atendimento.entities.ProfissionalSaude;
import br.org.apae.atendimento.exceptions.ProfissionalNotFoundException;
import br.org.apae.atendimento.repositories.ProfissionalSaudeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfissionalSaudeService {

    private final ProfissionalSaudeRepository profissionalSaudeRepository;

    public ProfissionalSaudeService(ProfissionalSaudeRepository profissionalSaudeRepository) {
        this.profissionalSaudeRepository = profissionalSaudeRepository;
    }

    public ProfissionalSaude getProfissionalById(Long id) {
        return profissionalSaudeRepository.findById(id).orElseThrow(() -> new ProfissionalNotFoundException("Profissional com ID " + id + " não encontrado."));
    }

    public List<Paciente> getPacienteDoProfissional (Long id) {
        ProfissionalSaude profissionalSaude = getProfissionalById(id);
        return profissionalSaude.getPacientes();
    }

    public String getPrimeiroNome (Long id) {
        ProfissionalSaude profissionalSaude = getProfissionalById(id);
        return profissionalSaude.getPrimeiroNome();
    }
}
