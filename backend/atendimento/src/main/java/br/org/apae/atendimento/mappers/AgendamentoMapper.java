package br.org.apae.atendimento.mappers;

import br.org.apae.atendimento.dtos.request.AgendamentoRequestDTO;
import br.org.apae.atendimento.dtos.response.AgendamentoResponseDTO;
import br.org.apae.atendimento.entities.Atendimento;
import br.org.apae.atendimento.entities.Paciente;
import br.org.apae.atendimento.entities.ProfissionalSaude;
import org.bouncycastle.asn1.ocsp.Request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AgendamentoMapper extends AbstractMapper<Atendimento, AgendamentoRequestDTO, AgendamentoResponseDTO> {
    @Override
    public Atendimento toEntityPadrao(AgendamentoRequestDTO dtoPadrao) {
        Atendimento atendimento = new Atendimento();

        ProfissionalSaude profissionalSaude = new ProfissionalSaude(dtoPadrao.profissionalId());
        Paciente paciente = new Paciente(dtoPadrao.pacientId());

        atendimento.setPaciente(paciente);
        atendimento.setProfissional(profissionalSaude);
        atendimento.setDataAtendimento(LocalDateTime.of(dtoPadrao.data(), dtoPadrao.hora()));
        atendimento.setStatus(false);
        atendimento.setNumeracao(dtoPadrao.numeroAtendimento());

        return atendimento;
    }

    @Override
    public AgendamentoResponseDTO toDTOPadrao(Atendimento entidadePadrao) {
        return new AgendamentoResponseDTO(
                entidadePadrao.getId(),
                entidadePadrao.getDataAtendimento().toLocalTime(),
                entidadePadrao.getDataAtendimento().toLocalDate(),
                entidadePadrao.getNumeracao()
        );
    }


}
