package br.org.apae.atendimento.services;

import java.util.List;
import java.util.UUID;

import br.org.apae.atendimento.entities.Atendimento;
import br.org.apae.atendimento.entities.Paciente;
import br.org.apae.atendimento.entities.ProfissionalSaude;
import br.org.apae.atendimento.repositories.AtendimentoRepository;
import org.springframework.stereotype.Service;

@Service
public class AtendimentoService {
    private AtendimentoRepository atendimentoRepository;
    private PacienteService pacienteService;
    private ProfissionalSaudeService profissionalSaudeService;

    public AtendimentoService(AtendimentoRepository atendimentoRepository,
                           PacienteService pacienteService,
                           ProfissionalSaudeService profissionalSaudeService){
        this.atendimentoRepository = atendimentoRepository;
        this.pacienteService = pacienteService;
        this.profissionalSaudeService = profissionalSaudeService;
    }

    public Atendimento addAtendimento(Atendimento atendimento, UUID pacienteId, UUID profissionalSaudeId){
        Paciente paciente = pacienteService.getPacienteById(pacienteId);
        ProfissionalSaude profissional = profissionalSaudeService.getProfissionalById(profissionalSaudeId);

        atendimento.setPaciente(paciente);
        atendimento.setProfissional(profissional);
        return atendimentoRepository.save(atendimento);
    }

    public List<Atendimento> getAtendimentosDoPaciente(UUID id){
        Paciente paciente = pacienteService.getPacienteById(id);
        return paciente.getAtendimentos();
    }
}