package br.org.apae.atendimento.services;

import br.org.apae.atendimento.dtos.request.LoginRequestDTO;
import br.org.apae.atendimento.entities.ProfissionalSaude;
import br.org.apae.atendimento.repositories.ProfissionalSaudeRepository;
import br.org.apae.atendimento.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final ProfissionalSaudeRepository profissionalRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(ProfissionalSaudeRepository profissionalRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.profissionalRepository = profissionalRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String autenticar(LoginRequestDTO requestDTO) {
        String emailSanitizado = requestDTO.email().trim().toLowerCase();

        ProfissionalSaude usuario = profissionalRepository.findByEmailIgnoreCase(emailSanitizado)
                .orElseThrow(() -> new BadCredentialsException("Credenciais inválidas"));

        if ("INATIVO".equalsIgnoreCase(usuario.getStatus())) {
            throw new BadCredentialsException("Usuário inativo no sistema. Procure a administração.");
        }

        if (!passwordEncoder.matches(requestDTO.password(), usuario.getSenha())) {
            throw new BadCredentialsException("Credenciais inválidas");
        }

        return jwtService.gerarToken(usuario);
    }
}