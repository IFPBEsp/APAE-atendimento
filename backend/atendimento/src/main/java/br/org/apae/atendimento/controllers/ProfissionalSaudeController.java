package br.org.apae.atendimento.controllers;

import br.org.apae.atendimento.dtos.response.PacienteOptionDTO;
import br.org.apae.atendimento.dtos.response.PacienteResponseDTO;
import br.org.apae.atendimento.dtos.response.ProfissionalResponseDTO;
import br.org.apae.atendimento.entities.Paciente;
import br.org.apae.atendimento.services.ProfissionalSaudeService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import br.org.apae.atendimento.security.UsuarioAutenticado;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/profissionais")
public class ProfissionalSaudeController {

    @Autowired
    private ProfissionalSaudeService profissionalSaudeService;

    @GetMapping
    public ResponseEntity<ProfissionalResponseDTO> buscarPorId(
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado) {
        ProfissionalResponseDTO profissional = profissionalSaudeService
                .getProfissionalByIdDTO(usuarioAutenticado.getId());
        return ResponseEntity.ok().body(profissional);
    }

    @GetMapping("/pacientes")
    public ResponseEntity<List<PacienteResponseDTO>> listarPacientesDoProfissional(
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado) {
        List<PacienteResponseDTO> pacientes = profissionalSaudeService
                .getPacientesDoProfissional(usuarioAutenticado.getId());
        return ResponseEntity.ok().body(pacientes);
    }

    @GetMapping("/primeiro-nome")
    public ResponseEntity<String> obterPrimeiroNome(@AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado) {
        String nome = profissionalSaudeService.getPrimeiroNome(usuarioAutenticado.getId());
        return ResponseEntity.ok().body(nome);
    }

    @GetMapping("/pacientes-option")
    public ResponseEntity<List<PacienteOptionDTO>> pacientesOption(
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado) {
        return ResponseEntity.ok().body(profissionalSaudeService.getPacienteOption(usuarioAutenticado.getId()));
    }
}
