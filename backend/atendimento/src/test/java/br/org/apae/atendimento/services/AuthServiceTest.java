package br.org.apae.atendimento.services;

import br.org.apae.atendimento.dtos.request.EsqueciSenhaRequestDTO;
import br.org.apae.atendimento.dtos.request.LoginRequestDTO;
import br.org.apae.atendimento.dtos.response.AutenticacaoResponseDTO;
import br.org.apae.atendimento.entities.ProfissionalSaude;
import br.org.apae.atendimento.repositories.ProfissionalSaudeRepository;
import br.org.apae.atendimento.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private ProfissionalSaudeRepository profissionalRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Test
    @DisplayName("Esqueci senha: deve atualizar a senha com hash quando e-mail e CPF conferem")
    void esqueciSenhaDeveAtualizarSenhaComHash() {
        UUID usuarioId = UUID.randomUUID();
        EsqueciSenhaRequestDTO requestDTO = new EsqueciSenhaRequestDTO("  USER@EMAIL.COM  ", "000.000.010-82", "novaSenha123");
        ProfissionalSaude usuario = mock(ProfissionalSaude.class);

        when(profissionalRepository.findByEmailIgnoreCase("user@email.com")).thenReturn(Optional.of(usuario));
        when(usuario.getAtivo()).thenReturn(true);
        when(usuario.getPerfil()).thenReturn("ATENDIMENTO");
        when(usuario.getCpf()).thenReturn("00000001082");
        when(usuario.getUsuarioId()).thenReturn(usuarioId);
        when(passwordEncoder.encode("novaSenha123")).thenReturn("novaSenhaHash");

        authService.esqueciSenha(requestDTO);

        verify(passwordEncoder).encode("novaSenha123");
        verify(profissionalRepository).definirSenhaPrimeiroAcesso(usuarioId, "novaSenhaHash");
    }

    @Test
    @DisplayName("Esqueci senha: deve lançar erro genérico quando e-mail não existe")
    void esqueciSenhaDeveLancarErroGenericoQuandoEmailNaoExiste() {
        EsqueciSenhaRequestDTO requestDTO = new EsqueciSenhaRequestDTO("inexistente@email.com", "000.000.010-82", "novaSenha123");
        when(profissionalRepository.findByEmailIgnoreCase("inexistente@email.com")).thenReturn(Optional.empty());

        BadCredentialsException ex = assertThrows(BadCredentialsException.class,
                () -> authService.esqueciSenha(requestDTO));

        assertEquals("Os dados informados não conferem", ex.getMessage());
        verify(passwordEncoder, never()).encode(anyString());
        verify(profissionalRepository, never()).definirSenhaPrimeiroAcesso(any(), anyString());
    }

    @Test
    @DisplayName("Esqueci senha: deve lançar erro genérico quando CPF não confere com o e-mail")
    void esqueciSenhaDeveLancarErroGenericoQuandoCpfNaoConfere() {
        EsqueciSenhaRequestDTO requestDTO = new EsqueciSenhaRequestDTO("user@email.com", "000.000.110-82", "novaSenha123");
        ProfissionalSaude usuario = mock(ProfissionalSaude.class);

        when(profissionalRepository.findByEmailIgnoreCase("user@email.com")).thenReturn(Optional.of(usuario));
        when(usuario.getAtivo()).thenReturn(true);
        when(usuario.getPerfil()).thenReturn("ATENDIMENTO");
        when(usuario.getCpf()).thenReturn("000.000.010-82");

        BadCredentialsException ex = assertThrows(BadCredentialsException.class,
                () -> authService.esqueciSenha(requestDTO));

        assertEquals("Os dados informados não conferem", ex.getMessage());
        verify(passwordEncoder, never()).encode(anyString());
        verify(profissionalRepository, never()).definirSenhaPrimeiroAcesso(any(), anyString());
    }

    @Test
    @DisplayName("Esqueci senha: deve usar a mesma mensagem genérica para usuário inativo")
    void esqueciSenhaDeveLancarErroGenericoQuandoUsuarioInativo() {
        EsqueciSenhaRequestDTO requestDTO = new EsqueciSenhaRequestDTO("user@email.com", "000.000.010-82", "novaSenha123");
        ProfissionalSaude usuario = mock(ProfissionalSaude.class);

        when(profissionalRepository.findByEmailIgnoreCase("user@email.com")).thenReturn(Optional.of(usuario));
        when(usuario.getAtivo()).thenReturn(false);
        when(usuario.getCpf()).thenReturn("000.000.010-82");

        BadCredentialsException ex = assertThrows(BadCredentialsException.class,
                () -> authService.esqueciSenha(requestDTO));

        assertEquals("Os dados informados não conferem", ex.getMessage());
        verify(profissionalRepository, never()).definirSenhaPrimeiroAcesso(any(), anyString());
    }

    @Test
    @DisplayName("Deve autenticar com sucesso e gerar token JWT")
    void deveAutenticarComSucesso() {
        LoginRequestDTO requestDTO = new LoginRequestDTO("  USER@EMAIL.COM  ", "senha123");
        ProfissionalSaude usuario = mock(ProfissionalSaude.class);

        when(profissionalRepository.findByEmailIgnoreCase("user@email.com")).thenReturn(Optional.of(usuario));
        when(usuario.getAtivo()).thenReturn(true);
        when(usuario.getPerfil()).thenReturn("ATENDIMENTO");
        when(usuario.getSenha()).thenReturn("hash");
        when(passwordEncoder.matches("senha123", "hash")).thenReturn(true);
        when(jwtService.gerarToken(usuario)).thenReturn("jwt-token");

        AutenticacaoResponseDTO response = authService.autenticar(requestDTO);
        String token = response.token();

        assertEquals("jwt-token", token);
        verify(profissionalRepository).findByEmailIgnoreCase("user@email.com");
        verify(passwordEncoder).matches("senha123", "hash");
        verify(jwtService).gerarToken(usuario);
    }

    @Test
    @DisplayName("Deve lançar erro para e-mail não encontrado")
    void deveLancarErroQuandoEmailNaoEncontrado() {
        LoginRequestDTO requestDTO = new LoginRequestDTO("inexistente@email.com", "senha123");
        when(profissionalRepository.findByEmailIgnoreCase("inexistente@email.com")).thenReturn(Optional.empty());

        BadCredentialsException ex = assertThrows(BadCredentialsException.class,
                () -> authService.autenticar(requestDTO));

        assertEquals("Credenciais inválidas", ex.getMessage());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).gerarToken(any());
    }

    @Test
    @DisplayName("Deve lançar erro quando usuário estiver inativo")
    void deveLancarErroQuandoUsuarioInativo() {
        LoginRequestDTO requestDTO = new LoginRequestDTO("user@email.com", "senha123");
        ProfissionalSaude usuario = mock(ProfissionalSaude.class);

        when(profissionalRepository.findByEmailIgnoreCase("user@email.com")).thenReturn(Optional.of(usuario));
        when(usuario.getAtivo()).thenReturn(false);

        BadCredentialsException ex = assertThrows(BadCredentialsException.class,
                () -> authService.autenticar(requestDTO));

        assertEquals("Credenciais inválidas", ex.getMessage());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).gerarToken(any());
    }

    @Test
    @DisplayName("Deve lançar erro quando senha for inválida")
    void deveLancarErroQuandoSenhaInvalida() {
        LoginRequestDTO requestDTO = new LoginRequestDTO("user@email.com", "senhaErrada");
        ProfissionalSaude usuario = mock(ProfissionalSaude.class);

        when(profissionalRepository.findByEmailIgnoreCase("user@email.com")).thenReturn(Optional.of(usuario));
        when(usuario.getAtivo()).thenReturn(true);
        when(usuario.getPerfil()).thenReturn("ATENDIMENTO");
        when(usuario.getSenha()).thenReturn("hash");
        when(passwordEncoder.matches("senhaErrada", "hash")).thenReturn(false);

        BadCredentialsException ex = assertThrows(BadCredentialsException.class,
                () -> authService.autenticar(requestDTO));

        assertEquals("Credenciais inválidas", ex.getMessage());
        verify(jwtService, never()).gerarToken(any());
    }
}
