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

        agendamento.setPacienteId(dtoPadrao.pacienteId());
        agendamento.setDataHora(LocalDateTime.of(dtoPadrao.data(), dtoPadrao.hora()));
        agendamento.setStatus(false);

        return agendamento;
    }

    @Override
    public AgendamentoResponseDTO toDTOPadrao(Agendamento agendamento) {
        String nomePaciente = agendamento.getPaciente() != null
                ? agendamento.getPaciente().getNomeCompleto()
                : null;

        return new AgendamentoResponseDTO(
                agendamento.getId(),
                agendamento.getPacienteId(),
                nomePaciente,
                agendamento.getDataHora().toLocalDate(),
                agendamento.getDataHora().toLocalTime(),
                agendamento.getNumeracao(),
                agendamento.isStatus(),
                false
        );
    }
}
