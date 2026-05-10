package br.org.apae.atendimento.services;

import br.org.apae.atendimento.dtos.request.AtendimentoRequestDTO;
import br.org.apae.atendimento.dtos.request.TopicoRequestDTO;
import br.org.apae.atendimento.dtos.response.AtendimentoResponseDTO;
import br.org.apae.atendimento.dtos.response.MesAnoAtendimentoResponseDTO;
import br.org.apae.atendimento.entities.Agendamento;
import br.org.apae.atendimento.entities.Atendimento;
import br.org.apae.atendimento.entities.Topico;
import br.org.apae.atendimento.exceptions.invalid.AtendimentoInvalidException;
import br.org.apae.atendimento.exceptions.invalid.RelacaoInvalidException;
import br.org.apae.atendimento.exceptions.invalid.TopicoInvalidException;
import br.org.apae.atendimento.exceptions.notfound.AgendamentoNotFoundException;
import br.org.apae.atendimento.exceptions.notfound.AtendimentoNotFoundException;
import br.org.apae.atendimento.mappers.AtendimentoMapper;
import br.org.apae.atendimento.repositories.AtendimentoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AtendimentoService {

    private final AtendimentoRepository repository;
    private final AgendamentoService agendamentoService;
    private final AtendimentoMapper atendimentoMapper;
    private final PacienteService pacienteService;

    public AtendimentoService(AtendimentoRepository repository,
                              AgendamentoService agendamentoService,
                              AtendimentoMapper atendimentoMapper,
                              PacienteService pacienteService) {
        this.repository = repository;
        this.agendamentoService = agendamentoService;
        this.atendimentoMapper = atendimentoMapper;
        this.pacienteService = pacienteService;
    }

    public AtendimentoResponseDTO addAtendimento(AtendimentoRequestDTO requestDTO, UUID profissionalId) {
        if (repository.existsByProfissionalIdAndDataAtendimento(
                profissionalId,
                LocalDateTime.of(requestDTO.data(), requestDTO.hora()))) {
            throw new AtendimentoInvalidException("Já existe um atendimento neste horário.");
        }

        Atendimento atendimento = atendimentoMapper.toEntityPadrao(requestDTO);
        atendimento.setProfissionalId(profissionalId);

        verificarRelatorio(atendimento.getRelatorio());

        atendimento.setNumeracao(gerarProximaNumeracao(requestDTO.data(), profissionalId));

        Atendimento persistido = repository.save(atendimento);

        try {
            tratarAgendamento(requestDTO.pacienteId(), requestDTO.data(), persistido.getNumeracao());
        } catch (AgendamentoNotFoundException e) {
            // Atendimento de encaixe sem agendamento prévio — ignorado intencionalmente
        }

        return atendimentoMapper.toDTOPadrao(persistido);
    }

    // Valida entidade já convertida — usada no addAtendimento
    private void verificarRelatorio(Set<Topico> relatorio) {
        if (relatorio == null || relatorio.isEmpty()) {
            throw new AtendimentoInvalidException("Atendimento sem qualquer tópico");
        }
        for (Topico topico : relatorio) {
            if (topico.getTitulo().isEmpty() || topico.getDescricao().isEmpty()) {
                throw new TopicoInvalidException("Tópico sem título ou descrição");
            }
        }
    }

    // Valida DTO — usada no editar antes da conversão
    private void verificarRelatorioDTO(Set<TopicoRequestDTO> relatorio) {
        if (relatorio == null || relatorio.isEmpty()) {
            throw new AtendimentoInvalidException("Atendimento sem qualquer tópico");
        }
        for (TopicoRequestDTO topico : relatorio) {
            if (topico.titulo().isEmpty() || topico.descricao().isEmpty()) {
                throw new TopicoInvalidException("Tópico sem título ou descrição");
            }
        }
    }

    public void tratarAgendamento(UUID pacienteId, LocalDate data, Long numeracao) {
        Agendamento agendamento = agendamentoService
                .buscarAgendamentoPorDataEPaciente(data, pacienteId);
        agendamento.setNumeracao(numeracao);
        agendamentoService.setStatus(agendamento);
    }

    public List<MesAnoAtendimentoResponseDTO> getAtendimentosAgrupadosPorMes(
            UUID pacienteId, UUID profissionalId) {

        List<Atendimento> atendimentos = repository
                .findByPacienteIdAndProfissionalIdOrderByDataAtendimento(pacienteId, profissionalId);

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
            throw new RelacaoInvalidException(
                    "Você não tem permissão para excluir atendimentos deste paciente.");
        }
        repository.deleteById(atendimentoId);
    }

    public Long gerarProximaNumeracao(LocalDate data, UUID profissionalId) {
        Long maiorNumeracao = repository.findMaxNumeracaoByMesAndAno(
                data.getMonthValue(), data.getYear(), profissionalId);
        return (maiorNumeracao != null ? maiorNumeracao : 0L) + 1;
    }

    public AtendimentoResponseDTO editar(AtendimentoRequestDTO requestDTO,
                                         UUID atendimentoId,
                                         UUID profissionalId) {
        if (!pacienteService.existeRelacao(requestDTO.pacienteId(), profissionalId)) {
            throw new RelacaoInvalidException(
                    "Você não tem permissão para editar atendimentos deste paciente.");
        }

        Atendimento atendimento = repository.findById(atendimentoId)
                .orElseThrow(() -> new AtendimentoNotFoundException(
                        "O atendimento que deseja editar não foi encontrado."));

        verificarRelatorioDTO(requestDTO.relatorio());

        atendimento.getRelatorio().clear();
        atendimento.getRelatorio().addAll(
                requestDTO.relatorio().stream()
                        .map(t -> Topico.from(t, atendimento))
                        .collect(Collectors.toCollection(HashSet::new))
        );

        if (!requestDTO.data().equals(atendimento.getDataAtendimento().toLocalDate())) {
            atendimento.setNumeracao(gerarProximaNumeracao(requestDTO.data(), profissionalId));
            atendimento.setDataAtendimento(
                    LocalDateTime.of(requestDTO.data(), requestDTO.hora()));
        }

        return atendimentoMapper.toDTOPadrao(repository.save(atendimento));
    }
}