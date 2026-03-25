package br.org.apae.atendimento.controllers;

import br.org.apae.atendimento.dtos.request.MagicLinkRequestDTO;
import br.org.apae.atendimento.services.AuthService;
import br.org.apae.atendimento.services.ProfissionalSaudeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final ProfissionalSaudeService profissionalService;

    public AuthController(@Autowired(required = false) AuthService authService, ProfissionalSaudeService profissionalSaudeService) {
        this.authService = authService;
        this.profissionalService = profissionalSaudeService;
    }

    @PostMapping("/send-link")
    public ResponseEntity<?> sendMagicLink(@Valid @RequestBody MagicLinkRequestDTO body) {
        String email = body.email();

        boolean emailExisteNoFirebase = authService == null || authService.emailExisteNoFirebase(email);

//        if (!profissionalService.existByEmail(email) ||  !emailExisteNoFirebase) {
//            return ResponseEntity
//                    .status(HttpStatus.FORBIDDEN)
//                    .body("Email não autorizado");
//        }

        return ResponseEntity.ok("Link autorizado");
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok("Logado");
    }
}
