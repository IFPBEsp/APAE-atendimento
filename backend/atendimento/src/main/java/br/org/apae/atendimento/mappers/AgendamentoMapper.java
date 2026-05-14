package br.org.apae.atendimento.mappers;

import br.org.apae.atendimento.dtos.request.AgendamentoRequestDTO;
import br.org.apae.atendimento.dtos.response.AgendamentoResponseDTO;
import br.org.apae.atendimento.entities.Agendamento;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AgendamentoMapper extends AbstractMapper<Agendamento, AgendamentoRequestDTO, AgendamentoResponseDTO> {
    @Override
    public Agendamento toEntityPadrao(AgendamentoRequestDTO dtoPadrao) {
        Agendamento agendamento = new Agendamento();

        agendamento.setDataHora(LocalDateTime.of(dtoPadrao.data(), dtoPadrao.hora()));
        agendamento.setStatus(false);

        return agendamento;
    }

    @Override
    public AgendamentoResponseDTO toDTOPadrao(Agendamento entidadePadrao) {
        return toDTOPadrao(entidadePadrao, "Paciente");
    }


    public AgendamentoResponseDTO toDTOPadrao(Agendamento entidadePadrao, String nomePaciente) {
        return new AgendamentoResponseDTO(
                entidadePadrao.getId(),
                entidadePadrao.getPacienteId(),
                nomePaciente,
                entidadePadrao.getDataHora().toLocalDate(),
                entidadePadrao.getDataHora().toLocalTime(),
                entidadePadrao.getNumeracao(),
                entidadePadrao.isStatus()
        );
    }


}
