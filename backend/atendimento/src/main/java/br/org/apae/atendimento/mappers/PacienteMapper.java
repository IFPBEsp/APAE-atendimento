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
        StringBuilder sb = new StringBuilder();
        sb.append(paciente.getCidade())
                .append(", ")
                .append(paciente.getRua())
                .append(", ")
                .append(paciente.getBairro())
                .append(", ")
                .append(paciente.getNumeroCasa())
                .append(".");

        String endereco = sb.toString();

        return new PacienteResponseDTO(
                paciente.getId(),
                paciente.getNomeCompleto(),
                paciente.getDataDeNascimento(),
                endereco,
                paciente.getContato(),
                paciente.getResponsaveis(),
                paciente.getTranstornos(),
                paciente.getCpf()
        );
    }
}