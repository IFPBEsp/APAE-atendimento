package br.org.apae.atendimento.controllers;

import br.org.apae.atendimento.dtos.request.LoginRequestDTO;
import br.org.apae.atendimento.dtos.request.RedefinirSenhaRequestDTO;
import br.org.apae.atendimento.dtos.response.LoginResponseDTO;
import br.org.apae.atendimento.dtos.response.AutenticacaoResponseDTO;
import br.org.apae.atendimento.security.JwtService;
import br.org.apae.atendimento.security.TokenBlocklistService;
import br.org.apae.atendimento.security.UsuarioAutenticado;
import br.org.apae.atendimento.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Arrays;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final String TOKEN_COOKIE_NAME = "token";

    private final AuthService authService;
    private final JwtService jwtService;
    private final TokenBlocklistService tokenBlocklistService;

    @Value("${jwt.expiration.minutes:30}")
    private long expirationMinutes;

    @Value("${jwt.cookie.secure:false}")
    private boolean cookieSecure;

    public AuthController(AuthService authService, JwtService jwtService,
                          TokenBlocklistService tokenBlocklistService) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.tokenBlocklistService = tokenBlocklistService;
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO body,
                                                  HttpServletResponse response) {
        
        AutenticacaoResponseDTO autenticacao = authService.autenticar(body);

        ResponseCookie cookie = ResponseCookie.from(TOKEN_COOKIE_NAME, autenticacao.token())
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(Duration.ofMinutes(expirationMinutes))
                .sameSite("Strict")
                .build();
        
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(new LoginResponseDTO(true, "Login realizado com sucesso", autenticacao.primerioAcesso(), autenticacao.redirectTo()));
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<LoginResponseDTO> redefinirSenha(
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado,
            @Valid @RequestBody RedefinirSenhaRequestDTO body
    ) {
        authService.redefinirSenha(usuarioAutenticado.getId(), body);
        return ResponseEntity.ok(new LoginResponseDTO(true, "Senha redefinida com sucesso"));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<LoginResponseDTO> logout(HttpServletRequest request,
                                                   HttpServletResponse response) {
        String token = extractTokenFromCookie(request);

        if (token != null && !token.isBlank() && jwtService.tokenValido(token)){
            String jti = jwtService.extrairJti(token);
            if (jti != null && !jti.isBlank()) {
                tokenBlocklistService.revogar(jti, jwtService.extrairExpiracao(token));
            }
        }

        ResponseCookie clearCookie = ResponseCookie.from(TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(Duration.ZERO)
                .sameSite("Strict")
                .build();
        
        response.addHeader(HttpHeaders.SET_COOKIE, clearCookie.toString());

        return ResponseEntity.ok(new LoginResponseDTO(true, "Logout realizado com sucesso"));
    }

    @GetMapping("/me")
    public ResponseEntity<LoginResponseDTO> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401)
                .body(new LoginResponseDTO(false, "Não autenticado"));
        }

        return ResponseEntity.ok(new LoginResponseDTO(true, "Autenticado"));
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        return Arrays.stream(cookies)
                .filter(c -> TOKEN_COOKIE_NAME.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}