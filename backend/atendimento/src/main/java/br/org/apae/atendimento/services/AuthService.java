package br.org.apae.atendimento.services;

import br.org.apae.atendimento.dtos.request.LoginRequestDTO;
import br.org.apae.atendimento.entities.ProfissionalSaude;
import br.org.apae.atendimento.repositories.ProfissionalSaudeRepository;
import br.org.apae.atendimento.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AuthService{

    private static final String MSG_CREDENCIAIS_INVALIDAS = "Credenciais inválidas";

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
        String emailSanitizado = requestDTO.email().trim().toLowerCase(Locale.ROOT);
        String senhaInformada = requestDTO.password() == null ? "" : requestDTO.password();

        ProfissionalSaude usuario = profissionalRepository.findByEmailIgnoreCase(emailSanitizado)
                .orElseThrow(() -> new BadCredentialsException(MSG_CREDENCIAIS_INVALIDAS));

        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new BadCredentialsException(MSG_CREDENCIAIS_INVALIDAS);
        }

        String senhaHash = usuario.getSenha();
        if (senhaHash == null || senhaHash.isBlank()) {
            throw new BadCredentialsException(MSG_CREDENCIAIS_INVALIDAS);
        }

        if (!passwordEncoder.matches(senhaInformada, senhaHash)) {
            throw new BadCredentialsException(MSG_CREDENCIAIS_INVALIDAS);
        }

        return jwtService.gerarToken(usuario);
    }
}
