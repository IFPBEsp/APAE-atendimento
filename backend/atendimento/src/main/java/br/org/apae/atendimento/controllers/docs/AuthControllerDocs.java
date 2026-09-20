package br.org.apae.atendimento.controllers.docs;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import br.org.apae.atendimento.dtos.request.LoginRequestDTO;
import br.org.apae.atendimento.dtos.request.RedefinirSenhaRequestDTO;
import br.org.apae.atendimento.dtos.response.LoginResponseDTO;
import br.org.apae.atendimento.security.UsuarioAutenticado;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Tag(name = "Autenticação", description = "Endpoints para gerenciamento de login, redefinição de senha e emissão de tokens JWT")
public interface AuthControllerDocs {

    @Operation(summary = "Realizar Login", description = "Autentica o usuário no sistema e retorna um token JWT salvo em cookie HTTP-Only.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição mal formatados")
    })
    ResponseEntity<LoginResponseDTO> login(
            @Valid @Parameter(description = "Credenciais de acesso do usuário", required = true) LoginRequestDTO body,
            @Parameter(hidden = true) HttpServletResponse response
    );

    @Operation(summary = "Redefinir Senha", description = "Permite a um usuário logado redefinir sua própria senha.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Senha redefinida com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    ResponseEntity<LoginResponseDTO> redefinirSenha(
            @Parameter(hidden = true) UsuarioAutenticado usuarioAutenticado,
            @Valid @Parameter(description = "Novos dados de senha", required = true) RedefinirSenhaRequestDTO body
    );

    @Operation(summary = "Realizar Logout", description = "Revoga o token atual (blocklist) e limpa o cookie de autenticação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout realizado com sucesso")
    })
    ResponseEntity<LoginResponseDTO> logout(
            @Parameter(hidden = true) HttpServletRequest request,
            @Parameter(hidden = true) HttpServletResponse response
    );

    @Operation(summary = "Verificar Autenticação", description = "Retorna o status atual de autenticação do usuário com base no cookie.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário autenticado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    ResponseEntity<LoginResponseDTO> me(
            @Parameter(hidden = true) Authentication authentication
    );
}