package br.org.apae.atendimento.controllers;

import br.org.apae.atendimento.dtos.response.PacienteResponseDTO;
import br.org.apae.atendimento.dtos.response.ProfissionalResponseDTO;
import br.org.apae.atendimento.entities.Paciente;
import br.org.apae.atendimento.services.ProfissionalSaudeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/profissionais")
public class ProfissionalSaudeController {

    @Autowired
    private ProfissionalSaudeService profissionalSaudeService;

    @GetMapping("/{id}")
    public ResponseEntity<ProfissionalResponseDTO> buscarPorId(@PathVariable UUID id) {
        ProfissionalResponseDTO profissional = profissionalSaudeService.getProfissionalByIdDTO(id);
        return ResponseEntity.ok().body(profissional);
    }

    @GetMapping("/{id}/pacientes")
    public ResponseEntity<List<PacienteResponseDTO>> listarPacientesDoProfissional(@PathVariable UUID id) {
        List<PacienteResponseDTO> pacientes = profissionalSaudeService.getPacientesDoProfissional(id);
        return ResponseEntity.ok().body(pacientes);
    }

    @GetMapping("/{id}/primeiro-nome")
    public ResponseEntity<String> obterPrimeiroNome(@PathVariable UUID id) {
        String nome = profissionalSaudeService.getPrimeiroNome(id);
        return ResponseEntity.ok().body(nome);
    }
}
