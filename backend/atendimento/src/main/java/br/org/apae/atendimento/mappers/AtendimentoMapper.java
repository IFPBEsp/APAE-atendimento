package br.org.apae.atendimento.mappers;

import br.org.apae.atendimento.dtos.request.AtendimentoRequestDTO;
import org.springframework.stereotype.Component;

import br.org.apae.atendimento.dtos.response.AtendimentoResponseDTO;
import br.org.apae.atendimento.entities.Atendimento;
import br.org.apae.atendimento.entities.Paciente;
import br.org.apae.atendimento.entities.ProfissionalSaude;

import java.time.LocalDateTime;

@Component
public class AtendimentoMapper extends AbstractMapper<Atendimento, AtendimentoRequestDTO, AtendimentoResponseDTO> {
    @Override
    public Atendimento toEntityPadrao(AtendimentoRequestDTO dtoPadraoAtendimento) {
       Atendimento atendimento = new Atendimento();

       ProfissionalSaude profissional = new ProfissionalSaude();
       profissional.setId(dtoPadraoAtendimento.profissionalId());

       Paciente paciente = new Paciente();
       paciente.setId(dtoPadraoAtendimento.pacienteId());

        LocalDateTime dataAtendimento = LocalDateTime.of(dtoPadraoAtendimento.data(), dtoPadraoAtendimento.hora());

       atendimento.setDataAtendimento(dataAtendimento);
       atendimento.setProfissional(profissional);
       atendimento.setPaciente(paciente);
       atendimento.setRelatorio(dtoPadraoAtendimento.relatorio());

       return atendimento;
    }

    @Override
    public AtendimentoResponseDTO toDTOPadrao(Atendimento entidadePadraoAtendimento) {
        return new AtendimentoResponseDTO(
                entidadePadraoAtendimento.getId(),
                entidadePadraoAtendimento.getRelatorio(),
                entidadePadraoAtendimento.getDataAtendimento().toLocalDate(),
                entidadePadraoAtendimento.getDataAtendimento().toLocalTime(),
                entidadePadraoAtendimento.getNumeracao()
        );
    }
}
