package br.org.apae.atendimento.controllers;

import br.org.apae.atendimento.dtos.response.ProfissionalResponseDTO;
import br.org.apae.atendimento.services.AuthService;
import br.org.apae.atendimento.services.ProfissionalSaudeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final ProfissionalSaudeService profissionalService;

    public AuthController(AuthService authService, ProfissionalSaudeService profissionalSaudeService) {
        this.authService = authService;
        this.profissionalService = profissionalSaudeService;
    }

    @PostMapping("/send-link")
    public ResponseEntity<?> sendMagicLink(@RequestBody Map<String, String> body) {

        String email = body.get("email");

        if (!profissionalService.existByEmail(email) ||  !authService.emailExisteNoFirebase(email)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Email não autorizado");
        }

        return ResponseEntity.ok("Link autorizado");
    }

    @GetMapping("/me")
    public ResponseEntity<ProfissionalResponseDTO> me(Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok().body(profissionalService.getProfissionalLogado());
    }
}
