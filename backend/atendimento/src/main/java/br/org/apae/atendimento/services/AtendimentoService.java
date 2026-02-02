package br.org.apae.atendimento.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import br.org.apae.atendimento.dtos.request.AtendimentoRequestDTO;
import br.org.apae.atendimento.dtos.response.AtendimentoResponseDTO;
import br.org.apae.atendimento.dtos.response.MesAnoAtendimentoResponseDTO;
import br.org.apae.atendimento.entities.Agendamento;
import br.org.apae.atendimento.entities.Atendimento;
import br.org.apae.atendimento.entities.ProfissionalSaude;
import br.org.apae.atendimento.entities.Topico;
import br.org.apae.atendimento.exceptions.invalid.TopicoInvalidException;
import br.org.apae.atendimento.exceptions.notfound.AgendamentoNotFoundException;
import br.org.apae.atendimento.exceptions.invalid.AtendimentoInvalidException;
import br.org.apae.atendimento.exceptions.invalid.RelacaoInvalidException;
import br.org.apae.atendimento.exceptions.notfound.AtendimentoNotFoundException;
import br.org.apae.atendimento.mappers.AtendimentoMapper;
import br.org.apae.atendimento.repositories.AtendimentoRepository;
import org.springframework.stereotype.Service;

@Service
public class AtendimentoService {
    private AtendimentoRepository repository;
    private AgendamentoService agendamentoService;
    private AtendimentoMapper atendimentoMapper;
    private PacienteService pacienteService;

    public AtendimentoService(AtendimentoRepository atendimentoRepository,
            AgendamentoService agendamentoService,
            AtendimentoMapper atendimentoMapper,
            PacienteService pacienteService) {

        this.repository = atendimentoRepository;
        this.agendamentoService = agendamentoService;
        this.atendimentoMapper = atendimentoMapper;
        this.pacienteService = pacienteService;
    }

    public AtendimentoResponseDTO addAtendimento(AtendimentoRequestDTO atendimentoRequestDTO, UUID profissionalId) {
        if (repository.existsByProfissionalIdAndDataAtendimento(
                profissionalId,
                LocalDateTime.of(atendimentoRequestDTO.data(), atendimentoRequestDTO.hora()))) {
            throw new AtendimentoInvalidException("Já existe um atendimento neste horário.");
        }

        Atendimento dadosConvertidos = atendimentoMapper.toEntityPadrao(atendimentoRequestDTO);

        ProfissionalSaude profissional = new ProfissionalSaude();
        profissional.setId(profissionalId);
        dadosConvertidos.setProfissional(profissional);

        verificarRelatorio(dadosConvertidos.getRelatorio());

        dadosConvertidos.setNumeracao(gerarProximaNumeracao(atendimentoRequestDTO.data(),
                profissionalId, atendimentoRequestDTO.pacienteId()));

        Atendimento dadosPersistidos = repository.save(dadosConvertidos);
        try {
            tratarAgendamento(atendimentoRequestDTO.pacienteId(), atendimentoRequestDTO.data(),
                    dadosPersistidos.getNumeracao());
        } catch (AgendamentoNotFoundException e) {
        }

        return atendimentoMapper.toDTOPadrao(dadosPersistidos);

    }

    private void verificarRelatorio(Set<Topico> relatorio) {
        if (relatorio == null || relatorio.isEmpty()) {
            throw new AtendimentoInvalidException("Atendimento sem qualquer tópico");
        }
        for (Topico topico : relatorio) {
            if (topico.getTitulo().isEmpty() || topico.getDescricao().isEmpty()) {
                throw new TopicoInvalidException("Topico sem titulo ou descrição");
            }
        }
    }

    public void tratarAgendamento(UUID pacienteId, LocalDate data, Long numeracao) {
        Agendamento agendamento = agendamentoService.buscarAgendamentoPorDataEPaciente(data, pacienteId);
        agendamento.setNumeracao(numeracao);

        agendamentoService.setStatus(agendamento);
    }

    public List<MesAnoAtendimentoResponseDTO> getAtendimentosAgrupadosPorMes(UUID pacienteId, UUID profissionalId) {

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
            throw new RelacaoInvalidException();
        }

        repository.deleteById(atendimentoId);
    }

    public Long gerarProximaNumeracao(LocalDate data, UUID profissionalId, UUID pacienteId) {
        Long maiorNumeracao = repository.findMaxNumeracaoByMesAndAno(
                data.getMonthValue(), data.getYear(), profissionalId, pacienteId);

        return maiorNumeracao + 1;
    }

    public AtendimentoResponseDTO editar(AtendimentoRequestDTO requestDTO, UUID atendimentoId, UUID profissionalId) {
        if (!pacienteService.existeRelacao(requestDTO.pacienteId(), profissionalId)) {
            throw new RelacaoInvalidException();
        }

        Atendimento atendimento = repository.findById(atendimentoId)
                .orElseThrow(() -> new AtendimentoNotFoundException());
        verificarRelatorio(requestDTO.relatorio());

        atendimento.getRelatorio().clear();
        atendimento.getRelatorio().addAll(requestDTO.relatorio());

        if (!requestDTO.data().equals(atendimento.getDataAtendimento().toLocalDate())) {
            atendimento.setNumeracao(gerarProximaNumeracao(
                    requestDTO.data(), profissionalId, requestDTO.pacienteId()));
            atendimento.setDataAtendimento(LocalDateTime.of(requestDTO.data(), requestDTO.hora()));
        }

        return atendimentoMapper.toDTOPadrao(repository.save(atendimento));

    }
}