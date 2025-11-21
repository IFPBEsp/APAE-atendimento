package br.org.apae.atendimento.controllers;

import br.org.apae.atendimento.dtos.AtendimentoRequestDTO;
import br.org.apae.atendimento.dtos.response.AtendimentoResponseDTO;
import br.org.apae.atendimento.services.AtendimentoService;
import br.org.apae.atendimento.services.PacienteService;
import br.org.apae.atendimento.services.ProfissionalSaudeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/atendimentos")
public class AtendimentoController {

    @Autowired
    private AtendimentoService atendimentoService;

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private ProfissionalSaudeService profissionalSaudeService;

    @PostMapping
    public ResponseEntity<AtendimentoResponseDTO> criarAtendimento(
            @RequestBody AtendimentoRequestDTO atendimento
    ) {
        AtendimentoResponseDTO novoAtendimento = atendimentoService.addAtendimento(atendimento);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoAtendimento);
    }

    @GetMapping("/paciente/{id}")
    public ResponseEntity<List<AtendimentoResponseDTO>> listarAtendimentosDoPaciente(@PathVariable UUID id) {
        List<AtendimentoResponseDTO> atendimentos = atendimentoService.getAtendimentosDoPaciente(id);
        return ResponseEntity.ok(atendimentos);
    }
}
