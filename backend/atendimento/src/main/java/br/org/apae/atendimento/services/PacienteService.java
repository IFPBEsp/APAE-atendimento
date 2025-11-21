package br.org.apae.atendimento.services;
import java.util.UUID;

import org.springframework.stereotype.Service;

import br.org.apae.atendimento.dtos.response.PacienteResponseDTO;
import br.org.apae.atendimento.entities.Paciente;
import br.org.apae.atendimento.exceptions.PacienteNotFoundException;
import br.org.apae.atendimento.mappers.PacienteMapper;
import br.org.apae.atendimento.repositories.PacienteRepository;

@Service
public class PacienteService {
    private PacienteRepository pacienteRepository;
    private PacienteMapper pacienteMapper;


    public PacienteService (PacienteRepository pacienteRepository, PacienteMapper pacienteMapper){
        this.pacienteRepository = pacienteRepository;
        this.pacienteMapper = pacienteMapper;
    }

    public PacienteResponseDTO getPaciente(UUID id){
        Paciente paciente = getPacienteById(id);
        return this.pacienteMapper.toDTOPadrao(paciente);
    }

    public Paciente getPacienteById(UUID id){
        return pacienteRepository.
        findById(id).orElseThrow(() -> new PacienteNotFoundException());
    }


}
