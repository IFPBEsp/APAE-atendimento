package br.org.apae.atendimento.services;

import br.org.apae.atendimento.dtos.request.AgendamentoRequestDTO;
import br.org.apae.atendimento.dtos.response.AgendamentoResponseDTO;
import br.org.apae.atendimento.dtos.response.DiaAgendamentoResponseDTO;
import br.org.apae.atendimento.entities.Agendamento;
import br.org.apae.atendimento.exceptions.invalid.AgendamentoInvalidException;
import br.org.apae.atendimento.exceptions.notfound.AgendamentoNotFoundException;
import br.org.apae.atendimento.exceptions.invalid.RelacaoInvalidException;
import br.org.apae.atendimento.mappers.AgendamentoMapper;
import br.org.apae.atendimento.repositories.AgendamentoGeralReadRepository;
import br.org.apae.atendimento.repositories.AgendamentoRepository;
import br.org.apae.atendimento.repositories.AtendimentoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import br.org.apae.atendimento.repositories.ProfissionalPacienteRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AgendamentoService {
    private AgendamentoRepository repository;
    private PacienteService pacienteService;
    private AgendamentoMapper agendamentoMapper;
    private AtendimentoRepository atendimentoRepository;
    private ProfissionalPacienteRepository profissionalPacienteRepository;
    private AgendamentoGeralReadRepository agendamentoGeralReadRepository;

    public AgendamentoService(AgendamentoRepository repository,
                              PacienteService pacienteService,
                              AgendamentoMapper agendamentoMapper,
                              AtendimentoRepository atendimentoRepository,
                              ProfissionalPacienteRepository profissionalPacienteRepository,
                              AgendamentoGeralReadRepository agendamentoGeralReadRepository) {

        this.repository = repository;
        this.pacienteService = pacienteService;
        this.agendamentoMapper = agendamentoMapper;
        this.atendimentoRepository = atendimentoRepository;
        this.profissionalPacienteRepository = profissionalPacienteRepository;
        this.agendamentoGeralReadRepository = agendamentoGeralReadRepository;
    }

    public Agendamento save(Agendamento agendamento) {
        return repository.save(agendamento);
    }

    @Transactional
    public AgendamentoResponseDTO agendar(
        AgendamentoRequestDTO agendamentoRequest, 
        UUID profissionalId
    ) {
        if (verificarAgendamentoExiste(
            profissionalId, 
            agendamentoRequest.data(), 
            agendamentoRequest.hora())
        ) {
            throw new AgendamentoInvalidException(
                    agendamentoRequest.data() + " - " + agendamentoRequest.hora() + " ja possui um agendamento");
        }

        Agendamento agendamento = agendamentoMapper.toEntityPadrao(agendamentoRequest);
        agendamento.setProfissionalId(profissionalId);

        verificarAtendimentos(
                agendamentoRequest.data(),
                profissionalId,
                agendamentoRequest.pacienteId(),
                agendamento
        );

        associarPacienteAoProfissional(
                profissionalId,
                agendamentoRequest.pacienteId()
        );

        return agendamentoMapper.toDTOPadrao(repository.save(agendamento));
    }

    public Agendamento buscarAgendamentoPorDataProfissionalEPaciente(
            LocalDate data,
            UUID profissionalId,
            UUID pacienteId
    ) {
        LocalDateTime dataInicio = data.atStartOfDay();
        LocalDateTime dataFim = dataInicio.plusDays(1);

        return repository.findByDataHoraAndProfissionalIdAndPacienteId(
                dataInicio,
                dataFim,
                profissionalId,
                pacienteId
        ).orElseThrow(() -> new AgendamentoNotFoundException(
                "Nenhum agendamento encontrado para esse profissional e paciente nesta data."
        ));
    }


    @Transactional
    public List<DiaAgendamentoResponseDTO> listarAgrupadoPorDia(UUID profissionalId) {
        List<AgendamentoResponseDTO> locais = repository.findByProfissionalIdOrderByDataHoraDesc(profissionalId)
                .stream()
                .map(agendamentoMapper::toDTOPadrao)
                .toList();

        List<AgendamentoResponseDTO> externos = agendamentoGeralReadRepository.findByProfissionalIdOrderByDataHoraDesc(profissionalId);

        List<AgendamentoResponseDTO> todosAgendamentos = new ArrayList<>(locais);
        todosAgendamentos.addAll(externos);

        return todosAgendamentos.stream()
                .collect(Collectors.groupingBy(
                        AgendamentoResponseDTO::data,
                        () -> new java.util.TreeMap<>(Comparator.reverseOrder()), 
                        Collectors.toList()
                ))
                .entrySet().stream()
                .map(e -> {
                    List<AgendamentoResponseDTO> ordenadosPorHora = e.getValue().stream()
                            .sorted(Comparator.comparing(AgendamentoResponseDTO::hora).reversed())
                            .toList();
                    return new DiaAgendamentoResponseDTO(e.getKey(), ordenadosPorHora);
                })
                .toList();
    }

    public void deletar(UUID profissionalId, UUID pacienteId, UUID agendamentoId) {
        if (!pacienteService.existeRelacao(pacienteId, profissionalId)) {
            throw new RelacaoInvalidException("Voce nao tem vinculo com este paciente para excluir o agendamento.");
        }

        Agendamento agendamento = repository
                .findByIdAndProfissionalIdAndPacienteId(agendamentoId, profissionalId, pacienteId)
                .orElseThrow(() -> new AgendamentoNotFoundException("O agendamento nao existe ou nao pertence ao profissional autenticado."));

        repository.delete(agendamento);
    }

    public void setStatus(Agendamento agendamento) {
        if (agendamento == null) {
            throw new AgendamentoNotFoundException("Não é possível alterar o status. Agendamento não encontrado.");
        }

        agendamento.setStatus(true);
        repository.save(agendamento);
    }

    public void concluir(UUID profissionalId, UUID pacienteId, UUID agendamentoId) {
        Agendamento agendamento = repository
                .findByIdAndProfissionalIdAndPacienteId(agendamentoId, profissionalId, pacienteId)
                .orElseThrow(() -> new AgendamentoNotFoundException("O agendamento nao existe ou nao pertence ao profissional autenticado."));

        setStatus(agendamento);
    }

    public boolean verificarAgendamentoExiste(UUID profissionalId, LocalDate data, LocalTime hora) {
        LocalDateTime dataHora = LocalDateTime.of(data, hora);
        return repository.existsByProfissionalIdAndDataHora(profissionalId, dataHora);
    }

    public void verificarAtendimentos(LocalDate data, UUID profissionalId, UUID pacienteId, Agendamento agendamento) {
        LocalDateTime inicioDia = data.atStartOfDay();
        LocalDateTime fimDia = data.plusDays(1).atStartOfDay();

        boolean existAtendimento = atendimentoRepository.existsAtendimentoNoDia(inicioDia,
                fimDia, profissionalId, pacienteId);

        Long numero = atendimentoRepository.findMaxNumeracaoByMesAndAno(
                data.getMonthValue(),
                data.getYear(),
                profissionalId
        );

        long numeracaoAtual = (numero != null) ? numero : 0L;

        if (existAtendimento) {
            agendamento.setNumeracao(String.valueOf(numeracaoAtual));
        } else {
            agendamento.setNumeracao(String.valueOf(numeracaoAtual + 1));
        }
    }

    private void associarPacienteAoProfissional(
            UUID profissionalId,
            UUID pacienteId
    ) {
        profissionalPacienteRepository.associarSeNaoExistir(profissionalId, pacienteId);
    }
}
