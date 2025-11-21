package br.org.apae.atendimento.controllers;

import br.org.apae.atendimento.dtos.response.PacienteResponseDTO;
import br.org.apae.atendimento.dtos.response.ProfissionalResponseDTO;
import br.org.apae.atendimento.services.ProfissionalSaudeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/profissionais")
public class ProfissionalSaudeController {

    @Autowired
    private ProfissionalSaudeService profissionalSaudeService;

    @GetMapping("/{id}")
    public ResponseEntity<ProfissionalResponseDTO> buscarPorId(@PathVariable Long id) {
        ProfissionalResponseDTO profissional = profissionalSaudeService.getProfissionalByIdDTO(id);
        return ResponseEntity.ok(profissional);
    }

    @GetMapping("/{id}/pacientes")
    public ResponseEntity<List<PacienteResponseDTO>> listarPacientesDoProfissional(@PathVariable Long id) {
        List<PacienteResponseDTO> pacientes = profissionalSaudeService.getPacientesDoProfissional(id);
        return ResponseEntity.ok(pacientes);
    }

    @GetMapping("/{id}/primeiro-nome")
    public ResponseEntity<String> obterPrimeiroNome(@PathVariable Long id) {
        String nome = profissionalSaudeService.getPrimeiroNome(id);
        return ResponseEntity.ok(nome);
    }
}
