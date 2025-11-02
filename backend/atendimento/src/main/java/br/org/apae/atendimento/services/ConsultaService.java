package br.org.apae.atendimento.services;

import java.util.List;

import br.org.apae.atendimento.entities.Consulta;
import br.org.apae.atendimento.entities.Paciente;
import br.org.apae.atendimento.entities.ProfissionalSaude;
import br.org.apae.atendimento.repositories.ConsultaRepository;

public class ConsultaService {
    private PacienteService pacienteService;
    private ConsultaRepository consultaRepository;

    public ConsultaService(ConsultaRepository consultaRepository, PacienteService pacienteService){
        this.consultaRepository= consultaRepository;
        this.pacienteService = pacienteService;
    }

    public Consulta addConsulta(Consulta consulta, Paciente paciente, ProfissionalSaude profissionalSaude){
        consulta.setPaciente(paciente);
        consulta.setProfissional(profissionalSaude);
        return consultaRepository.save(consulta);
    }

    public List<Consulta> getConsultasDoPaciente(Long id){
        Paciente paciente = pacienteService.getPacienteById(id);
        return paciente.getConsultas();
    }
}