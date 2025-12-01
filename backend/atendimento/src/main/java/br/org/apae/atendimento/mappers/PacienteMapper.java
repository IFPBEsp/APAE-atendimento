package br.org.apae.atendimento.mappers;

import org.springframework.stereotype.Component;

import br.org.apae.atendimento.dtos.response.PacienteResponseDTO;
import br.org.apae.atendimento.entities.Paciente;


@Component
public class PacienteMapper extends AbstractMapper<Paciente, Void, PacienteResponseDTO> {

    @Override
    public Paciente toEntityPadrao(Void dtoPadraoPaciente) {
        return null;
    }

    @Override
    public PacienteResponseDTO toDTOPadrao(Paciente paciente) {
        return new PacienteResponseDTO(
                paciente.getId(),
                paciente.getNomeCompleto(),
                paciente.getDataDeNascimento(),
                paciente.getContato(),
                paciente.getRua(),
                paciente.getBairro(),
                paciente.getNumeroCasa(),
                paciente.getResponsaveis(),
                paciente.getTranstornos(),
                paciente.getCidade(),
                paciente.getCpf()
        );
    }
}