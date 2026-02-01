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
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    public AgendamentoService(AgendamentoRepository repository,
                              PacienteService pacienteService,
                              ProfissionalSaudeService profissionalSaudeService,
                              AgendamentoMapper agendamentoMapper,
                              AtendimentoRepository atendimentoRepository){

        this.repository = repository;
        this.pacienteService = pacienteService;
        this.profissionalSaudeService = profissionalSaudeService;
        this.agendamentoMapper = agendamentoMapper;
        this.atendimentoRepository = atendimentoRepository;
    }

    public Agendamento save(Agendamento agendamento){
        return repository.save(agendamento);
    }

    public AgendamentoResponseDTO agendar(AgendamentoRequestDTO agendamentoRequest){
        if (verificarAgendamentoExiste(
                agendamentoRequest.profissionalId(),
                agendamentoRequest.data(), agendamentoRequest.hora())){
            throw new AgendamentoInvalidException(
                    agendamentoRequest.data() +  " - " + agendamentoRequest.hora() + " ja possui um agendamento");
        }

        Agendamento agendamento = agendamentoMapper.toEntityPadrao(agendamentoRequest);

        ProfissionalSaude profissionalSaude = profissionalSaudeService.getProfissionalById(agendamentoRequest.profissionalId());
        Paciente paciente = pacienteService.getPacienteById(agendamentoRequest.pacienteId());

        agendamento.setProfissional(profissionalSaude);
        agendamento.setPaciente(paciente);
        verificarAtendimentos(agendamentoRequest.data(),
                agendamentoRequest.profissionalId(),
                agendamentoRequest.pacienteId(),
                agendamento);

        return agendamentoMapper.toDTOPadrao(repository.save(agendamento));
    }

    public Agendamento buscarAgendamentoPorDataEPaciente(LocalDate data, UUID pacienteId){
        LocalDateTime dataInicio = data.atStartOfDay();
        LocalDateTime dataFim = dataInicio.plusDays(1);
        return repository.findByDataHoraAndPacienteId(dataInicio, dataFim, pacienteId)
                .orElseThrow(() -> new AgendamentoNotFoundException());
    }


    @Transactional
    public List<DiaAgendamentoResponseDTO> listarAgrupadoPorDia(UUID profissionalId) {
        return repository.findByProfissionalIdOrderByDataHora(profissionalId)
                .stream()
                .collect(Collectors.groupingBy(
                        a -> a.getDataHora().toLocalDate(),
                        Collectors.mapping(agendamentoMapper::toDTOPadrao, Collectors.toList())
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<LocalDate, List<AgendamentoResponseDTO>>comparingByKey().reversed())
                .map(e -> new DiaAgendamentoResponseDTO(e.getKey(), e.getValue()))
                .toList();
    }

    public void deletar(UUID profissionalId, UUID pacienteId, UUID agendamentoId){
        if (!pacienteService.existeRelacao(pacienteId, profissionalId)){
            throw new RelacaoInvalidException();
        }
        repository.deleteById(agendamentoId);
    }

    public void setStatus(Agendamento agendamento){
        if (agendamento == null){
            throw new AgendamentoNotFoundException();
        }

        agendamento.setStatus(true);
        repository.save(agendamento);
    }

    public boolean verificarAgendamentoExiste(UUID profissionalId, LocalDate data, LocalTime hora){
        LocalDateTime dataHora = LocalDateTime.of(data, hora);
        return repository.existsByProfissionalIdAndDataHora(profissionalId, dataHora);
    }

    public void verificarAtendimentos(LocalDate data, UUID profissionalId, UUID pacienteId, Agendamento agendamento){
        boolean existAtendimento = atendimentoRepository.existsAtendimentoNoDia(
                data.getDayOfMonth(), data.getMonthValue(), data.getYear(), profissionalId, pacienteId);

        if (existAtendimento){
            agendamento.setStatus(true);
            Long numero = atendimentoRepository.findMaxNumeracaoByMesAndAno(
                    data.getMonthValue(),
                    data.getYear(),
                    profissionalId,
                    pacienteId);

            agendamento.setNumeracao(numero);
        } else {
            Long numero = atendimentoRepository.findMaxNumeracaoByMesAndAno(
                    data.getMonthValue(),
                    data.getYear(),
                    profissionalId,
                    pacienteId);

            agendamento.setNumeracao(numero + 1);
        }
    }
}
