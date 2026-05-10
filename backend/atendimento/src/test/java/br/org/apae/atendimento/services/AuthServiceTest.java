package br.org.apae.atendimento.services;

import br.org.apae.atendimento.dtos.request.LoginRequestDTO;
import br.org.apae.atendimento.entities.views.ProfissionalSaude;
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
    @DisplayName("Deve autenticar com sucesso e gerar token JWT")
    void deveAutenticarComSucesso() {
        LoginRequestDTO requestDTO = new LoginRequestDTO("  USER@EMAIL.COM  ", "senha123");
        ProfissionalSaude usuario = mock(ProfissionalSaude.class);

        when(profissionalRepository.findByEmailIgnoreCase("user@email.com")).thenReturn(Optional.of(usuario));
        when(usuario.getStatus()).thenReturn("ATIVO");
      //  when(usuario.getSenha()).thenReturn("hash");
        when(passwordEncoder.matches("senha123", "hash")).thenReturn(true);
       // when(jwtService.gerarToken(usuario)).thenReturn("jwt-token");

        String token = authService.autenticar(requestDTO);

        assertEquals("jwt-token", token);
        verify(profissionalRepository).findByEmailIgnoreCase("user@email.com");
        verify(passwordEncoder).matches("senha123", "hash");
     //   verify(jwtService).gerarToken(usuario);
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
        when(usuario.getStatus()).thenReturn("INATIVO");

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
        when(usuario.getStatus()).thenReturn("ATIVO");
    //    when(usuario.getSenha()).thenReturn("hash");
        when(passwordEncoder.matches("senhaErrada", "hash")).thenReturn(false);

        BadCredentialsException ex = assertThrows(BadCredentialsException.class,
                () -> authService.autenticar(requestDTO));

        assertEquals("Credenciais inválidas", ex.getMessage());
        verify(jwtService, never()).gerarToken(any());
    }
}

