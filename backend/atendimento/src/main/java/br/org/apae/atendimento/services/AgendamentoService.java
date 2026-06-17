package br.org.apae.atendimento.services;

import br.org.apae.atendimento.dtos.request.AgendamentoRequestDTO;
import br.org.apae.atendimento.dtos.response.AgendamentoResponseDTO;
import br.org.apae.atendimento.dtos.response.DiaAgendamentoResponseDTO;
import br.org.apae.atendimento.entities.Agendamento;
import br.org.apae.atendimento.entities.Paciente;
import br.org.apae.atendimento.entities.ProfissionalSaude;
import br.org.apae.atendimento.exceptions.invalid.AgendamentoInvalidException;
import br.org.apae.atendimento.exceptions.notfound.AgendamentoNotFoundException;
import br.org.apae.atendimento.exceptions.invalid.RelacaoInvalidException;
import br.org.apae.atendimento.mappers.AgendamentoMapper;
import br.org.apae.atendimento.repositories.AgendamentoRepository;
import br.org.apae.atendimento.repositories.AtendimentoRepository;
import br.org.apae.atendimento.services.integration.AgendamentoExternoClient;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

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
    private ProfissionalSaudeService profissionalSaudeService;
    private AgendamentoMapper agendamentoMapper;
    private AtendimentoRepository atendimentoRepository;
    private AgendamentoExternoClient agendamentoExternoClient;

    public AgendamentoService(AgendamentoRepository repository,
                              PacienteService pacienteService,
                              ProfissionalSaudeService profissionalSaudeService,
                              AgendamentoMapper agendamentoMapper,
                              AtendimentoRepository atendimentoRepository,
                              AgendamentoExternoClient agendamentoExternoClient) { 

        this.repository = repository;
        this.pacienteService = pacienteService;
        this.profissionalSaudeService = profissionalSaudeService;
        this.agendamentoMapper = agendamentoMapper;
        this.atendimentoRepository = atendimentoRepository;
        this.agendamentoExternoClient = agendamentoExternoClient; 
    }

    public Agendamento save(Agendamento agendamento) {
        return repository.save(agendamento);
    }

    public AgendamentoResponseDTO agendar(AgendamentoRequestDTO agendamentoRequest, UUID profissionalId) {
        if (verificarAgendamentoExiste(
                profissionalId,
                agendamentoRequest.data(), agendamentoRequest.hora())) {
            throw new AgendamentoInvalidException(
                    agendamentoRequest.data() + " - " + agendamentoRequest.hora() + " ja possui um agendamento");
        }

        Agendamento agendamento = agendamentoMapper.toEntityPadrao(agendamentoRequest);

        ProfissionalSaude profissionalSaude = profissionalSaudeService.getProfissionalById(profissionalId);
        Paciente paciente = pacienteService.getPacienteById(agendamentoRequest.pacienteId());

        agendamento.setProfissional(profissionalSaude);
        agendamento.setPaciente(paciente);
        verificarAtendimentos(agendamentoRequest.data(),
                profissionalId,
                agendamentoRequest.pacienteId(),
                agendamento);

        return agendamentoMapper.toDTOPadrao(repository.save(agendamento));
    }

    public Agendamento buscarAgendamentoPorDataEPaciente(LocalDate data, UUID pacienteId) {
        LocalDateTime dataInicio = data.atStartOfDay();
        LocalDateTime dataFim = dataInicio.plusDays(1);
        return repository.findByDataHoraAndPacienteId(dataInicio, dataFim, pacienteId)
                .orElseThrow(() -> new AgendamentoNotFoundException("Nenhum agendamento encontrado para esse paciente nesta data."));
    }


    @Transactional
    public List<DiaAgendamentoResponseDTO> listarAgrupadoPorDia(UUID profissionalId) {
        List<AgendamentoResponseDTO> locais = repository.findByProfissionalIdOrderByDataHora(profissionalId)
                .stream()
                .map(agendamentoMapper::toDTOPadrao)
                .toList();

        List<AgendamentoResponseDTO> externos = agendamentoExternoClient.buscarAgendamentosPorDataHora(profissionalId);

        List<AgendamentoResponseDTO> todosAgendamentos = new ArrayList<>(locais);
        todosAgendamentos.addAll(externos);
        todosAgendamentos.sort(Comparator.comparing(AgendamentoResponseDTO::data)
                .thenComparing(AgendamentoResponseDTO::hora));

        return todosAgendamentos.stream()
                .collect(Collectors.groupingBy(
                        AgendamentoResponseDTO::data,
                        Collectors.toList()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<LocalDate, List<AgendamentoResponseDTO>>comparingByKey().reversed())
                .map(e -> {
                    List<AgendamentoResponseDTO> ordenadosPorHora = e.getValue().stream()
                            .sorted(Comparator.comparing(AgendamentoResponseDTO::hora))
                            .toList();
                    return new DiaAgendamentoResponseDTO(e.getKey(), ordenadosPorHora);
                })
                .toList();
    }

    public void deletar(UUID profissionalId, UUID pacienteId, UUID agendamentoId) {
        if (!pacienteService.existeRelacao(pacienteId, profissionalId)) {
            throw new RelacaoInvalidException("Você não tem vínculo com este paciente para excluir o agendamento.");
        }
        if (!repository.existsById(agendamentoId)) {
            throw new AgendamentoNotFoundException("O agendamento que tentou excluir não existe ou já foi apagado.");
        }
        repository.deleteById(agendamentoId);
    }

    public void setStatus(Agendamento agendamento) {
        if (agendamento == null) {
            throw new AgendamentoNotFoundException("Não é possível alterar o status. Agendamento não encontrado.");
        }

        agendamento.setStatus(true);
        repository.save(agendamento);
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

        agendamento.setStatus(true);

        if (existAtendimento) {
            agendamento.setNumeracao(String.valueOf(numeracaoAtual));
        } else {
            agendamento.setNumeracao(String.valueOf(numeracaoAtual + 1));
        }
    }
}
