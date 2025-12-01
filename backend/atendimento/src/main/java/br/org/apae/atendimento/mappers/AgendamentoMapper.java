package br.org.apae.atendimento.mappers;

import br.org.apae.atendimento.dtos.request.AgendamentoRequestDTO;
import br.org.apae.atendimento.dtos.response.AgendamentoResponseDTO;
import br.org.apae.atendimento.entities.Agendamento;
import br.org.apae.atendimento.entities.Atendimento;
import br.org.apae.atendimento.entities.Paciente;
import br.org.apae.atendimento.entities.ProfissionalSaude;
import org.bouncycastle.asn1.ocsp.Request;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AgendamentoMapper extends AbstractMapper<Agendamento, AgendamentoRequestDTO, AgendamentoResponseDTO> {
    @Override
    public Agendamento toEntityPadrao(AgendamentoRequestDTO dtoPadrao) {
        Agendamento agendamento = new Agendamento();

        agendamento.setDataHora(LocalDateTime.of(dtoPadrao.data(), dtoPadrao.hora()));
        agendamento.setStatus(false);
        agendamento.setNumeracao(dtoPadrao.numeroAtendimento());

        return agendamento;
    }

    @Override
    public AgendamentoResponseDTO toDTOPadrao(Agendamento entidadePadrao) {
        return new AgendamentoResponseDTO(
                entidadePadrao.getId(),
                entidadePadrao.getPaciente().getId(),
                entidadePadrao.getPaciente().getNomeCompleto(),
                entidadePadrao.getDataHora().toLocalDate(),
                entidadePadrao.getDataHora().toLocalTime(),
                entidadePadrao.getNumeracao(),
                entidadePadrao.isStatus()
        );
    }


}
