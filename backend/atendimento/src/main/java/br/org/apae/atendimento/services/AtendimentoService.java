package br.org.apae.atendimento.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import br.org.apae.atendimento.dtos.request.AtendimentoRequestDTO;
import br.org.apae.atendimento.dtos.response.AtendimentoResponseDTO;
import br.org.apae.atendimento.entities.Atendimento;
import br.org.apae.atendimento.entities.Paciente;
import br.org.apae.atendimento.entities.ProfissionalSaude;
import br.org.apae.atendimento.mappers.AtendimentoMapper;
import br.org.apae.atendimento.repositories.AtendimentoRepository;
import org.springframework.stereotype.Service;

@Service
public class AtendimentoService {
    private AtendimentoRepository atendimentoRepository;
    private PacienteService pacienteService;
    private ProfissionalSaudeService profissionalSaudeService;
    private AtendimentoMapper atendimentoMapper;

    public AtendimentoService(AtendimentoRepository atendimentoRepository,
                              PacienteService pacienteService,
                              ProfissionalSaudeService profissionalSaudeService,
                              AtendimentoMapper atendimentoMapper){

        this.atendimentoRepository = atendimentoRepository;
        this.pacienteService = pacienteService;
        this.profissionalSaudeService = profissionalSaudeService;
        this.atendimentoMapper = atendimentoMapper;
    }

    public AtendimentoResponseDTO addAtendimento(AtendimentoRequestDTO atendimentoRequestDTO){
        Atendimento dadosConvertidos = atendimentoMapper.toEntityPadrao(atendimentoRequestDTO);
        Atendimento dadosPersistidos = atendimentoRepository.save(dadosConvertidos);

        return atendimentoMapper.toDTOPadrao(dadosPersistidos);
    }

    public List<AtendimentoResponseDTO> getAtendimentosDoPaciente(UUID id){
        Paciente paciente = pacienteService.getPacienteById(id);
        return paciente.getAtendimentos().stream()
                                  .map(atendimento -> atendimentoMapper.toDTOPadrao(atendimento))
                                  .collect(Collectors.toList());
    }
}