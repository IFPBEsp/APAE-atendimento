package br.org.apae.atendimento.services;
import br.org.apae.atendimento.dtos.response.PacienteResponseDTO;
import br.org.apae.atendimento.entities.Paciente;
import br.org.apae.atendimento.exceptions.PacienteNotFoundException;
import br.org.apae.atendimento.mappers.PacienteMapper;
import br.org.apae.atendimento.repositories.PacienteRepository;
import org.springframework.stereotype.Service;

@Service
public class PacienteService {
    private PacienteRepository pacienteRepository;
    private PacienteMapper pacienteMapper;


    public PacienteService (PacienteRepository pacienteRepository, PacienteMapper pacienteMapper){
        this.pacienteRepository = pacienteRepository;
        this.pacienteMapper = pacienteMapper;
    }

    public PacienteResponseDTO getPacienteById(Long id){
        Paciente paciente = pacienteRepository.
        findById(id).orElseThrow(() -> new PacienteNotFoundException());
        return this.pacienteMapper.toDTOPadrao(paciente);
    }



}
