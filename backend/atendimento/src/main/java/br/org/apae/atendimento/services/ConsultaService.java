package br.org.apae.atendimento.services;

import java.util.List;

import br.org.apae.atendimento.entities.Consulta;
import br.org.apae.atendimento.entities.Paciente;
import br.org.apae.atendimento.entities.ProfissionalSaude;
import br.org.apae.atendimento.repositories.ConsultaRepository;
import br.org.apae.atendimento.repositories.PacienteRepository;
import br.org.apae.atendimento.repositories.ProfissionalSaudeRepository;

public class ConsultaService {
    private ConsultaRepository consultaRepository;
    private PacienteService pacienteService;
    private ProfissionalSaudeService profissionalSaudeService;

    public ConsultaService(ConsultaRepository consultaRepository,
                           PacienteService pacienteService,
                           ProfissionalSaudeService profissionalSaudeService){
        this.consultaRepository = consultaRepository;
        this.pacienteService = pacienteService;
        this.profissionalSaudeService = profissionalSaudeService;
    }

    public Consulta addConsulta(Consulta consulta, Long pacienteId, Long profissionalSaudeId){
        Paciente paciente = pacienteService.getPacienteById(pacienteId);
        ProfissionalSaude profissional = profissionalSaudeService.getProfissionalById(profissionalSaudeId);

        consulta.setPaciente(paciente);
        consulta.setProfissional(profissional);
        return consultaRepository.save(consulta);
    }

    public List<Consulta> getConsultasDoPaciente(Long id){
        Paciente paciente = pacienteService.getPacienteById(id);
        return paciente.getConsultas();
    }
}
