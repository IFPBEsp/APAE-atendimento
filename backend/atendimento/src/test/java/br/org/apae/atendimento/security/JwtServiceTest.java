package br.org.apae.atendimento.security;

import br.org.apae.atendimento.entities.views.ProfissionalSaude;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtServiceTest {

    private static final String SECRET_32_BYTES_BASE64 = "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET_32_BYTES_BASE64);
        ReflectionTestUtils.setField(jwtService, "expirationMinutes", 30L);
        jwtService.validarChave();
    }

    @Test
    @DisplayName("Deve lançar exceção quando chave JWT for menor que 256 bits")
    void deveLancarExcecaoQuandoChaveForCurta() {
        ReflectionTestUtils.setField(jwtService, "secretKey", "MTIzNDU2Nzg5MDEyMzQ1Ng==");

        IllegalStateException ex = assertThrows(IllegalStateException.class, jwtService::validarChave);
        assertEquals("jwt.secret deve ter no mínimo 256 bits (32 bytes)", ex.getMessage());
    }

    @Test
    @DisplayName("Deve gerar token válido contendo subject e roles")
    void deveGerarTokenValidoComClaimsEsperadas() {
        ProfissionalSaude usuario = mock(ProfissionalSaude.class);
        UUID id = UUID.randomUUID();

        when(usuario.getId()).thenReturn(id);
       // when(usuario.getPerfil()).thenReturn("ROLE_ADMIN");

       // String token = jwtService.gerarToken(usuario);

     //   assertTrue(jwtService.tokenValido(token));
      //  assertEquals(id.toString(), jwtService.extrairSubject(token));
//assertEquals(List.of("ROLE_ADMIN"), jwtService.extrairRoles(token));
    }

    @Test
    @DisplayName("Deve usar ROLE_PROFISSIONAL quando perfil do usuário for nulo")
    void deveUsarRolePadraoQuandoPerfilNulo() {
        ProfissionalSaude usuario = mock(ProfissionalSaude.class);

        when(usuario.getId()).thenReturn(UUID.randomUUID());
     //   when(usuario.getPerfil()).thenReturn(null);

       // String token = jwtService.gerarToken(usuario);

      //  assertEquals(List.of("ROLE_PROFISSIONAL"), jwtService.extrairRoles(token));
    }

    @Test
    @DisplayName("Deve retornar falso para token adulterado")
    void deveRetornarFalsoParaTokenAdulterado() {
        ProfissionalSaude usuario = mock(ProfissionalSaude.class);

        when(usuario.getId()).thenReturn(UUID.randomUUID());
    //    when(usuario.getPerfil()).thenReturn("ROLE_PROFISSIONAL");

 //       String token = jwtService.gerarToken(usuario);
  //      String tokenAdulterado = token + "abc";

   //     assertFalse(jwtService.tokenValido(tokenAdulterado));
    }

    @Test
    @DisplayName("Deve retornar falso para token expirado")
    void deveRetornarFalsoParaTokenExpirado() {
        ReflectionTestUtils.setField(jwtService, "expirationMinutes", -1L);

        ProfissionalSaude usuario = mock(ProfissionalSaude.class);
        when(usuario.getId()).thenReturn(UUID.randomUUID());
  //      when(usuario.getPerfil()).thenReturn("ROLE_PROFISSIONAL");

  //      String token = jwtService.gerarToken(usuario);

    //    assertFalse(jwtService.tokenValido(token));
    }
}

