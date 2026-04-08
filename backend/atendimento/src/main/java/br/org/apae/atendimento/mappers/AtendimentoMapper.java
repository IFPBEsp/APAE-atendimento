package br.org.apae.atendimento.mappers;

import br.org.apae.atendimento.dtos.request.AtendimentoRequestDTO;
import br.org.apae.atendimento.repositories.PacienteRepository;
import org.springframework.stereotype.Component;

import br.org.apae.atendimento.dtos.response.AtendimentoResponseDTO;
import br.org.apae.atendimento.entities.Atendimento;
import br.org.apae.atendimento.entities.Paciente;

import java.time.LocalDateTime;

@Component
public class AtendimentoMapper extends AbstractMapper<Atendimento, AtendimentoRequestDTO, AtendimentoResponseDTO> {

    private final PacienteRepository pacienteRepository;

    public AtendimentoMapper(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @Override
    public Atendimento toEntityPadrao(AtendimentoRequestDTO dtoPadraoAtendimento) {
        Atendimento atendimento = new Atendimento();

        Paciente paciente = pacienteRepository.getReferenceById(dtoPadraoAtendimento.pacienteId());

        LocalDateTime dataAtendimento = LocalDateTime.of(dtoPadraoAtendimento.data(), dtoPadraoAtendimento.hora());

        atendimento.setDataAtendimento(dataAtendimento);
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
                entidadePadraoAtendimento.getNumeracao());
    }
}
