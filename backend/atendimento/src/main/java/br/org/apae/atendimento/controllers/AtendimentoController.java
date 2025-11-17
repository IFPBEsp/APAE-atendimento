package br.org.apae.atendimento.controllers;

import br.org.apae.atendimento.entities.Atendimento;
import br.org.apae.atendimento.services.AtendimentoService;
import br.org.apae.atendimento.services.PacienteService;
import br.org.apae.atendimento.services.ProfissionalSaudeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Atendimento> criarAtendimento(
            @RequestBody Atendimento atendimento,
            @RequestParam Long pacienteId,
            @RequestParam Long profissionalId
    ) {
        Atendimento novoAtendimento = atendimentoService.addAtendimento(atendimento, pacienteId, profissionalId);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoAtendimento);
    }

    @GetMapping("/paciente/{id}")
    public ResponseEntity<List<Atendimento>> listarAtendimentosDoPaciente(@PathVariable Long id) {
        List<Atendimento> atendimentos = atendimentoService.getAtendimentosDoPaciente(id);
        return ResponseEntity.ok(atendimentos);
    }
}
