package br.org.apae.atendimento.controllers;

import br.org.apae.atendimento.dtos.response.PacienteOptionDTO;
import br.org.apae.atendimento.dtos.response.PacienteResponseDTO;
import br.org.apae.atendimento.dtos.response.ProfissionalDropdownResponseDTO;
import br.org.apae.atendimento.dtos.response.ProfissionalResponseDTO;
import br.org.apae.atendimento.security.UsuarioAutenticado;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@Tag(name = "Profissional de Saúde", description = "Endpoints para consulta de dados do profissional de saúde autenticado")
public interface ProfissionalSaudeControllerDocs {

    @Operation(summary = "Buscar profissional autenticado", description = "Retorna os dados do profissional de saúde atualmente autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profissional encontrado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Profissional de saúde não encontrado")
    })
    ResponseEntity<ProfissionalResponseDTO> buscarPorId(
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado
    );

    @Operation(summary = "Listar pacientes do profissional", description = "Retorna a lista completa de pacientes vinculados ao profissional autenticado, incluindo a URL da foto de cada paciente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pacientes retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    ResponseEntity<List<PacienteResponseDTO>> listarPacientesDoProfissional(
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado
    );

    @Operation(summary = "Listar pacientes em formato de opção", description = "Retorna uma lista simplificada dos pacientes do profissional autenticado, em formato reduzido para uso em seleções (options).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    ResponseEntity<List<PacienteOptionDTO>> pacientesOption(
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado
    );

    @Operation(summary = "Listar profissionais para dropdown", description = "Retorna uma lista simplificada de todos os profissionais de saúde, para uso em componentes de seleção (dropdown).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    ResponseEntity<List<ProfissionalDropdownResponseDTO>> listarParaDropdown();
}