package br.org.apae.atendimento.services;

import br.org.apae.atendimento.dtos.request.LoginRequestDTO;
import br.org.apae.atendimento.entities.CredenciaisProfissional;
import br.org.apae.atendimento.entities.views.ProfissionalSaude;
import br.org.apae.atendimento.repositories.CredenciaisProfissionalRepository;
import br.org.apae.atendimento.repositories.ProfissionalSaudeRepository;
import br.org.apae.atendimento.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AuthService {

    private static final String MSG_CREDENCIAIS_INVALIDAS = "Credenciais inválidas";

    private final CredenciaisProfissionalRepository credenciaisRepository;
    private final ProfissionalSaudeRepository profissionalRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(CredenciaisProfissionalRepository credenciaisRepository,
                       ProfissionalSaudeRepository profissionalRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.credenciaisRepository = credenciaisRepository;
        this.profissionalRepository = profissionalRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String autenticar(LoginRequestDTO requestDTO) {
        String emailSanitizado = requestDTO.email().trim().toLowerCase(Locale.ROOT);
        String senhaInformada = requestDTO.password() == null ? "" : requestDTO.password();

        // 1. Busca profissional pelo email na view
        ProfissionalSaude profissional = profissionalRepository
                .findByEmailIgnoreCase(emailSanitizado)
                .orElseThrow(() -> new BadCredentialsException(MSG_CREDENCIAIS_INVALIDAS));

        // 2. Valida status antes de qualquer coisa
        if (!"ATIVO".equalsIgnoreCase(profissional.getStatus())) {
            throw new BadCredentialsException(MSG_CREDENCIAIS_INVALIDAS);
        }

        // 3. Busca credenciais pelo id do profissional
        CredenciaisProfissional credenciais = credenciaisRepository
                .findById(profissional.getId())
                .orElseThrow(() -> new BadCredentialsException(MSG_CREDENCIAIS_INVALIDAS));

        // 4. Valida senha
        if (!passwordEncoder.matches(senhaInformada, credenciais.getSenha())) {
            throw new BadCredentialsException(MSG_CREDENCIAIS_INVALIDAS);
        }

        // 5. Gera token
        return jwtService.gerarToken(credenciais);
    }
}