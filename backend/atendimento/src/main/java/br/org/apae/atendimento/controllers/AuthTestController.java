package br.org.apae.atendimento.controllers;

import br.org.apae.atendimento.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthTestController {

    private final AuthService authService;

    public AuthTestController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/send-link")
    public ResponseEntity<?> sendMagicLink(@RequestBody Map<String, String> body) {

        String email = body.get("email");

        if (!authService.emailExisteNoFirebase(email)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Email não autorizado");
        }

        return ResponseEntity.ok("Link autorizado");
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        return ResponseEntity.ok(Map.of(
                "email", authentication.getName()
        ));
    }
}
