package br.org.apae.atendimento.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import br.org.apae.atendimento.dtos.request.AtendimentoRequestDTO;
import br.org.apae.atendimento.dtos.request.TopicoRequestDTO;
import br.org.apae.atendimento.dtos.response.AtendimentoResponseDTO;
import br.org.apae.atendimento.dtos.response.MesAnoAtendimentoResponseDTO;
import br.org.apae.atendimento.entities.Agendamento;
import br.org.apae.atendimento.entities.Atendimento;
import br.org.apae.atendimento.entities.Topico;
import br.org.apae.atendimento.exceptions.invalid.TopicoInvalidException;
import br.org.apae.atendimento.exceptions.notfound.AgendamentoNotFoundException;
import br.org.apae.atendimento.exceptions.invalid.AtendimentoInvalidException;
import br.org.apae.atendimento.exceptions.invalid.RelacaoInvalidException;
import br.org.apae.atendimento.exceptions.notfound.AtendimentoNotFoundException;
import br.org.apae.atendimento.mappers.AtendimentoMapper;
import br.org.apae.atendimento.repositories.AtendimentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AtendimentoService {
    private final AtendimentoRepository repository;
    private final AgendamentoService agendamentoService;
    private final AtendimentoMapper atendimentoMapper;
    private final PacienteService pacienteService;

    public AtendimentoService(AtendimentoRepository repository,
                              AgendamentoService agendamentoService,
                              AtendimentoMapper atendimentoMapper,
                              PacienteService pacienteService
                              ) {

        this.repository = repository;
        this.agendamentoService = agendamentoService;
        this.atendimentoMapper = atendimentoMapper;
        this.pacienteService = pacienteService;
    }

    public AtendimentoResponseDTO addAtendimento(AtendimentoRequestDTO atendimentoRequestDTO, UUID profissionalId) {
        if (!pacienteService.existeRelacao(atendimentoRequestDTO.pacienteId(), profissionalId)) {
            throw new RelacaoInvalidException("Você não tem permissão para criar atendimentos deste paciente.");
        }

        if (repository.existsByProfissionalIdAndDataAtendimento(
                profissionalId,
                LocalDateTime.of(atendimentoRequestDTO.data(), atendimentoRequestDTO.hora())
        )) {
            throw new AtendimentoInvalidException("Já existe um atendimento neste horário.");
        }

        Atendimento dadosConvertidos = atendimentoMapper.toEntityPadrao(atendimentoRequestDTO);
        dadosConvertidos.setProfissionalId(profissionalId);

        verificarRelatorio(atendimentoRequestDTO.relatorio());

        dadosConvertidos.setNumeracao(gerarProximaNumeracao(profissionalId, atendimentoRequestDTO.data()));

        Atendimento dadosPersistidos = repository.save(dadosConvertidos);
        try {
            tratarAgendamento(
                    profissionalId,
                    atendimentoRequestDTO.pacienteId(),
                    atendimentoRequestDTO.data(),
                    dadosPersistidos.getNumeracao()
            );
        } catch (AgendamentoNotFoundException e) {
        }

        return atendimentoMapper.toDTOPadrao(dadosPersistidos);

    }

    private void verificarRelatorio(List<TopicoRequestDTO> relatorio) {
        if (relatorio == null || relatorio.isEmpty()) {
            throw new AtendimentoInvalidException("Atendimento sem qualquer tópico");
        }
        for (TopicoRequestDTO topico : relatorio) {
            if (topico.titulo().isEmpty() || topico.descricao().isEmpty()) {
                throw new TopicoInvalidException("Topico sem titulo ou descrição");
            }
        }
    }

    public void tratarAgendamento(UUID profissionalId, UUID pacienteId, LocalDate data, String numeracao) {
        Agendamento agendamento = agendamentoService.buscarAgendamentoPorDataProfissionalEPaciente(
                data,
                profissionalId,
                pacienteId
        );

        agendamento.setNumeracao(numeracao);
        agendamentoService.setStatus(agendamento);
    }

    public List<MesAnoAtendimentoResponseDTO> getAtendimentosAgrupadosPorMes(UUID pacienteId, UUID profissionalId) {
        if (!pacienteService.existeRelacao(pacienteId, profissionalId)) {
            throw new RelacaoInvalidException("Você não tem permissão para listar atendimentos deste paciente.");
        }

        List<Atendimento> atendimentos = repository
                .findByPacienteIdAndProfissionalIdComRelatorio(pacienteId, profissionalId);

        return atendimentos.stream()
                .collect(Collectors.groupingBy(
                        a -> YearMonth.from(a.getDataAtendimento()),
                        Collectors.mapping(atendimentoMapper::toDTOPadrao, Collectors.toList())))
                .entrySet().stream()
                .map(e -> new MesAnoAtendimentoResponseDTO(e.getKey(), e.getValue()))
                .toList();
    }

    public void deletar(UUID profissionalId, UUID pacienteId, UUID atendimentoId) {
        if (!pacienteService.existeRelacao(pacienteId, profissionalId)) {
            throw new RelacaoInvalidException("Você não tem permissão para excluir atendimentos deste paciente.");
        }

        Atendimento atendimento = repository
                .findByIdAndProfissionalIdAndPacienteId(atendimentoId, profissionalId, pacienteId)
                .orElseThrow(() -> new AtendimentoNotFoundException("O atendimento não foi encontrado."));

        repository.delete(atendimento);
    }

    public String gerarProximaNumeracao(UUID profissionalId, LocalDate data) {
        Long maiorNumeracao = repository.findMaxNumeracaoByMesAndAno(
                data.getMonthValue(),
                data.getYear(),
                profissionalId);

        long numeracaoAtual = (maiorNumeracao != null) ? maiorNumeracao : 0L;

        return String.valueOf(numeracaoAtual + 1);
    }

    @Transactional(readOnly = false)
    public AtendimentoResponseDTO editar(AtendimentoRequestDTO requestDTO, UUID atendimentoId, UUID profissionalId) {
        Atendimento atendimento = repository.findByIdComRelatorio(atendimentoId)
                .orElseThrow(() -> new AtendimentoNotFoundException("O atendimento que deseja editar não foi encontrado."));

        if (!pacienteService.existeRelacao(requestDTO.pacienteId(), profissionalId)) {
            throw new RelacaoInvalidException("Voce nao tem permissao para editar atendimentos deste paciente.");
        }

        if (!atendimento.getProfissionalId().equals(profissionalId)) {
            throw new RelacaoInvalidException("Voce nao tem permissao para editar este atendimento.");
        }

        verificarRelatorio(requestDTO.relatorio());

        atendimento.getRelatorio().clear();
        requestDTO.relatorio().forEach(t -> {
            Topico topico = new Topico();
            topico.setTitulo(t.titulo());
            topico.setDescricao(t.descricao());
            atendimento.getRelatorio().add(topico);
        });

        if (!requestDTO.data().equals(atendimento.getDataAtendimento().toLocalDate())
                || !requestDTO.hora().equals(atendimento.getDataAtendimento().toLocalTime())) {
            atendimento.setDataAtendimento(LocalDateTime.of(requestDTO.data(), requestDTO.hora()));
        }

        atendimento.setPacienteId(requestDTO.pacienteId());

        return atendimentoMapper.toDTOPadrao(repository.save(atendimento));
    }

    @Transactional
    public void concluirAtendimento(UUID profissionalId, UUID atendimentoId) {
        int atualizados = repository.concluirAtendimento(atendimentoId, profissionalId);

        if (atualizados == 0) {
            throw new AtendimentoNotFoundException("O atendimento nao existe ou nao pertence ao profissional autenticado.");
        }
    }
}
