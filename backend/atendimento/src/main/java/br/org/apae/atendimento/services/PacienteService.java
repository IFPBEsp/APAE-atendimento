package br.org.apae.atendimento.services;
import br.org.apae.atendimento.entities.Paciente;
import br.org.apae.atendimento.exceptions.PacienteNotFoundException;
import br.org.apae.atendimento.repositories.PacienteRepository;


public class PacienteService {
    private PacienteRepository pacienteRepository;

    public PacienteService (PacienteRepository pacienteRepository){
        this.pacienteRepository = pacienteRepository;
    }

    public Paciente getPacienteById(Long id){
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new PacienteNotFoundException());
    }

}
