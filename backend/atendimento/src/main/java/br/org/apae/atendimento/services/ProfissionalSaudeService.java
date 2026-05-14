package br.org.apae.atendimento.services;

import java.util.List;
import java.util.UUID;

import br.org.apae.atendimento.dtos.response.PacienteOptionDTO;
import br.org.apae.atendimento.dtos.response.ProfissionalDropdownResponseDTO;
import br.org.apae.atendimento.exceptions.notfound.ProfissionalSaudeNotFoundException;
import br.org.apae.atendimento.mappers.PacienteMapper;
import br.org.apae.atendimento.mappers.ProfissionalMapper;

import br.org.apae.atendimento.repositories.AtendimentoRepository;
import br.org.apae.atendimento.repositories.PacienteRepository;
import br.org.apae.atendimento.services.storage.PresignedUrlService;
import org.springframework.stereotype.Service;

import br.org.apae.atendimento.dtos.response.PacienteResponseDTO;
import br.org.apae.atendimento.dtos.response.ProfissionalResponseDTO;
import br.org.apae.atendimento.entities.views.Paciente;
import br.org.apae.atendimento.entities.views.ProfissionalSaude;
import br.org.apae.atendimento.repositories.ProfissionalSaudeRepository;
import org.springframework.transaction.annotation.Transactional;

import static java.util.stream.Collectors.toList;

@Service
public class ProfissionalSaudeService {

    private final ProfissionalSaudeRepository repository;
    private final ProfissionalMapper profissionalMapper;
    private final PacienteMapper pacienteMapper;
    private final PacienteRepository pacienteRepository;
    private final AtendimentoRepository atendimentoRepository;
    private final PresignedUrlService urlService;
    private final PacienteService pacienteService;

    private static String FOTO_PATH = "foto/";

    public ProfissionalSaudeService(ProfissionalSaudeRepository profissionalSaudeRepository,
                                    ProfissionalMapper profissionalMapper,
                                    PacienteMapper pacienteMapper,
                                    PacienteRepository pacienteRepository,
                                    PresignedUrlService urlService,
                                    AtendimentoRepository atendimentoRepository,
                                    PacienteService pacienteService) {
        this.repository = profissionalSaudeRepository;
        this.pacienteMapper = pacienteMapper;
        this.profissionalMapper = profissionalMapper;
        this.pacienteRepository = pacienteRepository;
        this.urlService = urlService;
        this.atendimentoRepository = atendimentoRepository;
        this.pacienteService = pacienteService;
    }

    public ProfissionalSaude getProfissionalById(UUID id) {
        ProfissionalSaude profissionalSaude = repository.findById(id)
                .orElseThrow(() -> new ProfissionalSaudeNotFoundException("Profissional de saúde não encontrado."));

        return profissionalSaude;
    }

    public ProfissionalResponseDTO getProfissionalByIdDTO(UUID id) {
        ProfissionalSaude profissionalSaude = getProfissionalById(id);
        return profissionalMapper.toDTOPadrao(profissionalSaude);
    }

    public List<PacienteResponseDTO> getPacientesDoProfissional(UUID profissionalId) {
        getProfissionalById(profissionalId);

        List<Paciente> pacientes = atendimentoRepository.findPacientesByProfissionalId(profissionalId);

        return pacientes.stream()
                .map(p -> pacienteMapper.toDTOCompleto(p, null, null, null))
                .toList();
    }
    public List<PacienteOptionDTO> getPacienteOption(UUID profissionalId) {
        List<Paciente> pacientes = pacienteRepository.findByProfissionalId(profissionalId);
        return pacientes.stream()
                .map(paciente -> pacienteMapper.toOptionDTO(paciente))
                .collect(toList());
    }

    public String getPrimeiroNome(UUID id) {
        String nomeCompleto = repository.findNomeCompletoById(id);
        if (nomeCompleto == null || nomeCompleto.isBlank()) {
            return "Doutor(a)";
        }
        // Pega a primeira palavra do nome completo
        return nomeCompleto.trim().split("\\s+")[0];
    }

    @Transactional(readOnly = true)
    public List<ProfissionalDropdownResponseDTO> listarParaDropdown() {
        return repository.listarParaDropdown();
    }
}
