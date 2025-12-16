package br.org.apae.atendimento.controllers;

import br.org.apae.atendimento.dtos.response.PacienteResponseDTO;
import br.org.apae.atendimento.services.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> buscarPorId(@PathVariable UUID id) {
        PacienteResponseDTO paciente = pacienteService.getPaciente(id);
        return ResponseEntity.ok(paciente);
    }
    @GetMapping("/{id}/nome-completo")
    public ResponseEntity<String> obterPrimeiroNome(@PathVariable UUID id) {
        String nome = pacienteService.getNomeCompletoPacienteById(id);
        return ResponseEntity.ok().body(nome);
    }
}
