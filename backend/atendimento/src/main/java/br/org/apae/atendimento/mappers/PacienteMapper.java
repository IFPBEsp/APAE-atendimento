package br.org.apae.atendimento.mappers;

import br.org.apae.atendimento.dtos.response.PacienteOptionDTO;
import br.org.apae.atendimento.dtos.response.PacienteResponseDTO;
import br.org.apae.atendimento.entities.views.EnderecoPaciente;
import br.org.apae.atendimento.entities.views.Paciente;
import br.org.apae.atendimento.entities.views.ResponsavelPaciente;
import br.org.apae.atendimento.entities.views.TranstornoPaciente;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PacienteMapper {

    // Montagem completa — service passa todos os dados resolvidos
    public PacienteResponseDTO toDTOCompleto(
            Paciente paciente,
            EnderecoPaciente endereco,
            List<ResponsavelPaciente> responsaveis,
            List<TranstornoPaciente> transtorno)
            //String fotoPreAssinada)
            {

        // Endereço formatado — null-safe caso paciente não tenha endereço cadastrado
        String enderecoFormatado = endereco != null
                ? endereco.getRua()
                  + ", " + endereco.getNumeroCasa()
                  + ", " + endereco.getBairro()
                  + ", " + endereco.getCidade() + "."
                : "Endereço não informado";

        // Responsáveis como lista de nomes — null-safe
        List<String> nomesResponsaveis = responsaveis != null
                ? responsaveis.stream()
                  .map(ResponsavelPaciente::getNome)
                  .toList()
                : List.of();

        // Transtornos e alergias — null-safe
        List<String> transtornos = transtorno != null
                ? transtorno.stream()
                  .map(TranstornoPaciente::getTranstornos)
                  .toList()
                :List.of();

        return new PacienteResponseDTO(
                paciente.getId(),
                paciente.getNomeCompleto(),
                paciente.getDataDeNascimento(),
                enderecoFormatado,
                paciente.getContato(),
                nomesResponsaveis,
                transtornos,
                paciente.getCpf()
                //fotoPreAssinada
        );
    }

    // Versão leve — apenas dados básicos, sem joins
    // Usada em dropdowns, listagens e contextos onde endereço não é necessário
    public PacienteOptionDTO toOptionDTO(Paciente paciente) {
        return new PacienteOptionDTO(paciente.getId(), paciente.getNomeCompleto());
    }
}