package br.org.apae.atendimento.services;

import br.org.apae.atendimento.dtos.request.LoginRequestDTO;
import br.org.apae.atendimento.dtos.request.RedefinirSenhaRequestDTO;
import br.org.apae.atendimento.dtos.response.AutenticacaoResponseDTO;
import br.org.apae.atendimento.entities.ProfissionalSaude;
import br.org.apae.atendimento.exceptions.notfound.ProfissionalSaudeNotFoundException;
import br.org.apae.atendimento.repositories.ProfissionalSaudeRepository;
import br.org.apae.atendimento.security.JwtService;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Service
public class AuthService{

    private static final String CARGO_ATENDIMENTO = "ATENDIMENTO";
    private static final String MSG_CREDENCIAIS_INVALIDAS = "Credenciais inválidas";
    private static final String REDIRECT_PRIMEIRO_ACESSO = "/auth/redefinir-senha";

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
    
    public AutenticacaoResponseDTO autenticar(LoginRequestDTO requestDTO) {
        String emailSanitizado = requestDTO.email().trim().toLowerCase(Locale.ROOT);
        String senhaInformada = requestDTO.password() == null ? "" : requestDTO.password();

        ProfissionalSaude usuario = profissionalRepository.findByEmailIgnoreCase(emailSanitizado)
                .orElseThrow(() -> new BadCredentialsException(MSG_CREDENCIAIS_INVALIDAS));

        validarUsuarioAtendimento(usuario);

        boolean primeiroAcesso = Boolean.TRUE.equals(usuario.getPrimeiroAcesso());

        if (primeiroAcesso) {
            validarSenhaCpf(usuario, senhaInformada);
        } else {
            validarSenhaCadastrada(usuario, senhaInformada);
        }

        return new AutenticacaoResponseDTO(
                jwtService.gerarToken(usuario),
                primeiroAcesso,
                primeiroAcesso ? REDIRECT_PRIMEIRO_ACESSO : null
        );
    }

    @Transactional
    public void redefinirSenha(UUID profissionalId, RedefinirSenhaRequestDTO requestDTO) {
        ProfissionalSaude profissional = profissionalRepository.findById(profissionalId)
                .orElseThrow(ProfissionalSaudeNotFoundException::new);

        validarUsuarioAtendimento(profissional);

        String senhaHash = passwordEncoder.encode(requestDTO.novaSenha());
        profissionalRepository.definirSenhaPrimeiroAcesso(profissional.getUsuarioId(), senhaHash);
    }

    private void validarUsuarioAtendimento(ProfissionalSaude usuario) {
        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new BadCredentialsException(MSG_CREDENCIAIS_INVALIDAS);
        }

        if (usuario.getPerfil() == null ||
                !CARGO_ATENDIMENTO.equals(usuario.getPerfil().trim().toUpperCase(Locale.ROOT))) {
            throw new BadCredentialsException(MSG_CREDENCIAIS_INVALIDAS);
        }
    }

    private void validarSenhaCpf(ProfissionalSaude usuario, String senhaInformada) {
        String cpf = somenteDigitos(usuario.getCpf());
        String senha = somenteDigitos(senhaInformada);

        if (cpf.isBlank() || !cpf.equals(senha)) {
            throw new BadCredentialsException(MSG_CREDENCIAIS_INVALIDAS);
        }
    }

    private void validarSenhaCadastrada(ProfissionalSaude usuario, String senhaInformada) {
        String senhaHash = usuario.getSenha();

        if (senhaHash == null || senhaHash.isBlank()) {
            throw new BadCredentialsException(MSG_CREDENCIAIS_INVALIDAS);
        }

        if (!passwordEncoder.matches(senhaInformada, senhaHash)) {
            throw new BadCredentialsException(MSG_CREDENCIAIS_INVALIDAS);
        }
    }

    private String somenteDigitos(String valor) {
        return valor == null ? "" : valor.replaceAll("\\D", "");
    }
}
