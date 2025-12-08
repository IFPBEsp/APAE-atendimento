package br.org.apae.atendimento.services;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import br.org.apae.atendimento.dtos.request.AtendimentoRequestDTO;
import br.org.apae.atendimento.dtos.request.update.AtendimentoUpdateRequestDTO;
import br.org.apae.atendimento.dtos.response.AtendimentoResponseDTO;
import br.org.apae.atendimento.dtos.response.MesAnoAtendimentoResponseDTO;
import br.org.apae.atendimento.entities.Agendamento;
import br.org.apae.atendimento.entities.Atendimento;
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
                              PacienteService pacienteService
                              ){

        this.repository = atendimentoRepository;
        this.agendamentoService = agendamentoService;
        this.atendimentoMapper = atendimentoMapper;
        this.pacienteService = pacienteService;
    }

    public AtendimentoResponseDTO addAtendimento(AtendimentoRequestDTO atendimentoRequestDTO, UUID agendamentoId){
        Atendimento dadosConvertidos = atendimentoMapper.toEntityPadrao(atendimentoRequestDTO);

        verificarRelatorio(dadosConvertidos.getRelatorio());

        dadosConvertidos.setNumeracao(gerarProximaNumeracao(atendimentoRequestDTO.data(),
                atendimentoRequestDTO.profissionalId(), atendimentoRequestDTO.pacienteId()
        ));

        Atendimento dadosPersistidos = repository.save(dadosConvertidos);
        try {
            tratarAgendamento(agendamentoId, atendimentoRequestDTO.pacienteId(), atendimentoRequestDTO.data());
        }
        catch (AgendamentoNotFoundException e){
        }

        return atendimentoMapper.toDTOPadrao(dadosPersistidos);
    }

    private void verificarRelatorio(Map<String, Object> relatorio) {
        if (relatorio.isEmpty()){
            throw new AtendimentoInvalidException("Atendimento sem qualquer tópico");
        }
    }

    public void tratarAgendamento(UUID agendamentoId, UUID pacienteId, LocalDate data){
        if (agendamentoId != null){
            agendamentoService.setStatus(agendamentoId);
        }

        Agendamento agendamento = agendamentoService.buscarAgendamentoPorDataEPaciente(data, pacienteId);

        agendamentoService.setStatus(agendamento);
    }

    public List<MesAnoAtendimentoResponseDTO> getAtendimentosAgrupadosPorMes(UUID pacienteId, UUID profissionalId) {

        List<Atendimento> atendimentos = repository
                .findByPacienteIdAndProfissionalIdOrderByDataAtendimentoDesc(pacienteId, profissionalId);

        return atendimentos.stream()
                .collect(Collectors.groupingBy(
                        a -> YearMonth.from(a.getDataAtendimento()),
                        Collectors.mapping(atendimentoMapper::toDTOPadrao, Collectors.toList())
                ))
                .entrySet().stream()
                .map(e -> new MesAnoAtendimentoResponseDTO(e.getKey(), e.getValue()))
                .toList();
    }

    public void deletar(UUID profissionalId, UUID pacienteId, UUID atendimentoId){
        if (!pacienteService.existeRelacao(pacienteId, profissionalId)){
            throw new RelacaoInvalidException();
        }

        repository.deleteById(atendimentoId);
    }

    public Long gerarProximaNumeracao(LocalDate data, UUID profissionalId, UUID pacienteId){
        Long maiorNumeracao = repository.findMaxNumeracaoByMesAndAno(
                data.getMonthValue(), data.getYear(), profissionalId, pacienteId);

        return maiorNumeracao + 1;
    }

    public AtendimentoResponseDTO editarTopicos(AtendimentoUpdateRequestDTO requestDTO){
        Atendimento atendimento = repository.findById(requestDTO.atendimentoId())
                .orElseThrow(AtendimentoNotFoundException::new);

        String tituloAntigo = requestDTO.tituloAntigo();
        Map<String, String> novoTopico = requestDTO.update();

        if (novoTopico.size() != 1) {
            throw new IllegalArgumentException("Envie apenas um tópico por atualização.");
        }

        String novoTitulo = novoTopico.keySet().iterator().next();
        String novoConteudo = novoTopico.get(novoTitulo);

        Map<String, Object> relatorio = atendimento.getRelatorio();

        if (relatorio.containsKey(tituloAntigo)) {
            relatorio.remove(tituloAntigo);
            relatorio.put(novoTitulo, novoConteudo);
        }
        else {
            relatorio.put(novoTitulo, novoConteudo);
        }

        Atendimento atualizado = repository.save(atendimento);

        return atendimentoMapper.toDTOPadrao(atualizado);
    }
}