package br.org.apae.atendimento.mappers;

import br.org.apae.atendimento.dtos.request.AtendimentoRequestDTO;
import br.org.apae.atendimento.dtos.response.TopicoResponseDTO;
import br.org.apae.atendimento.entities.Topico;
import org.springframework.stereotype.Component;

import br.org.apae.atendimento.dtos.response.AtendimentoResponseDTO;
import br.org.apae.atendimento.entities.Atendimento;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AtendimentoMapper extends AbstractMapper<Atendimento, AtendimentoRequestDTO, AtendimentoResponseDTO> {

    @Override
    public Atendimento toEntityPadrao(AtendimentoRequestDTO dto) {
        Atendimento atendimento = new Atendimento();

        atendimento.setPacienteId(dto.pacienteId());
        atendimento.setDataAtendimento(LocalDateTime.of(dto.data(), dto.hora()));

        List<Topico> relatorio = dto.relatorio().stream()
                .map(t -> {
                    Topico topico = new Topico();
                    topico.setTitulo(t.titulo());
                    topico.setDescricao(t.descricao());
                    topico.setAtendimento(atendimento);
                    return topico;
                })
                .collect(Collectors.toList());

        atendimento.setRelatorio(relatorio);

        return atendimento;
    }

    @Override
    public AtendimentoResponseDTO toDTOPadrao(Atendimento entidadePadraoAtendimento) {
        List<TopicoResponseDTO> relatorio = entidadePadraoAtendimento.getRelatorio().stream()
                .map(t -> new TopicoResponseDTO(
                        t.getId(),
                        t.getTitulo(),
                        t.getDescricao()
                ))
                .collect(Collectors.toList());

        return new AtendimentoResponseDTO(
                entidadePadraoAtendimento.getId(),
                relatorio,
                entidadePadraoAtendimento.getDataAtendimento().toLocalDate(),
                entidadePadraoAtendimento.getDataAtendimento().toLocalTime(),
                entidadePadraoAtendimento.getNumeracao(),
                entidadePadraoAtendimento.isStatus());
    }
}
