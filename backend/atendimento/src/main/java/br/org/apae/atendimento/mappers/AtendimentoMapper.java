package br.org.apae.atendimento.mappers;

import br.org.apae.atendimento.dtos.AtendimentoRequestDTO;
import br.org.apae.atendimento.dtos.response.AtendimentoResponseDTO;
import br.org.apae.atendimento.entities.Atendimento;
import br.org.apae.atendimento.entities.Paciente;
import br.org.apae.atendimento.entities.ProfissionalSaude;

public class AtendimentoMapper extends AbstractMapper<Atendimento, AtendimentoRequestDTO, AtendimentoResponseDTO> {
    @Override
    public Atendimento toEntityPadrao(AtendimentoRequestDTO dtoPadraoAtendimento) {
       Atendimento atendimento = new Atendimento();

       ProfissionalSaude profissional = new ProfissionalSaude();
       profissional.setId(dtoPadraoAtendimento.profissionalId());

       Paciente paciente = new Paciente();
       paciente.setId(dtoPadraoAtendimento.pacienteId());

       atendimento.setDataAtendimento(dtoPadraoAtendimento.dataAtendimento());
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
                entidadePadraoAtendimento.getDataAtendimento(),
                entidadePadraoAtendimento.isStatus()
        );
    }
}
