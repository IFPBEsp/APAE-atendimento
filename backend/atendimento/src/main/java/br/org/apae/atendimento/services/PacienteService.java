package br.org.apae.atendimento.services;
import java.util.UUID;

import org.springframework.stereotype.Service;

import br.org.apae.atendimento.dtos.response.PacienteResponseDTO;
import br.org.apae.atendimento.entities.Paciente;
import br.org.apae.atendimento.exceptions.notfound.PacienteNotFoundException;
import br.org.apae.atendimento.mappers.PacienteMapper;
import br.org.apae.atendimento.repositories.PacienteRepository;

@Service
public class PacienteService {
    private PacienteRepository repository;
    private PacienteMapper pacienteMapper;


    public PacienteService (PacienteRepository pacienteRepository, PacienteMapper pacienteMapper){
        this.repository = pacienteRepository;
        this.pacienteMapper = pacienteMapper;
    }

    public PacienteResponseDTO getPaciente(UUID id){
        Paciente paciente = getPacienteById(id);
        return this.pacienteMapper.toDTOPadrao(paciente);
    }

    public Paciente getPacienteById(UUID id){
        return repository
                .findById(id).orElseThrow(() -> new PacienteNotFoundException());
    }

    public boolean existeRelacao(UUID pacienteId, UUID profissionalId){
        return repository.existeRelacao(pacienteId, profissionalId);
    }

}
