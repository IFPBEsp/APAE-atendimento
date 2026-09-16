package br.org.apae.atendimento.controllers;

import br.org.apae.atendimento.dtos.response.PacienteDropdownResponseDTO;
import br.org.apae.atendimento.dtos.response.PacienteResponseDTO;
import br.org.apae.atendimento.dtos.response.PaginatedResponseDTO;
import br.org.apae.atendimento.security.UsuarioAutenticado;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "Paciente", description = "Endpoints para consulta e gerenciamento de pacientes")
public interface PacienteControllerDocs {

    @Operation(summary = "Buscar paciente por ID", description = "Retorna os dados completos de um paciente vinculado ao profissional autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paciente encontrado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado para o profissional autenticado")
    })
    ResponseEntity<PacienteResponseDTO> buscarPorId(
            @Parameter(description = "ID do paciente") UUID id,
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado
    );

    @Operation(summary = "Obter nome completo do paciente", description = "Retorna apenas o nome completo de um paciente vinculado ao profissional autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nome retornado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado para o profissional autenticado")
    })
    ResponseEntity<String> obterNomeCompleto(
            @Parameter(description = "ID do paciente") UUID id,
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado
    );

    @Operation(summary = "Buscar pacientes do profissional", description = "Retorna uma lista paginada de pacientes vinculados ao profissional autenticado, podendo ser filtrada por nome, CPF e cidade.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pacientes retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    ResponseEntity<PaginatedResponseDTO<PacienteResponseDTO>> buscarPacientes(
            @Parameter(description = "Filtro opcional por nome") String nome,
            @Parameter(description = "Filtro opcional por CPF") String cpf,
            @Parameter(description = "Filtro opcional por cidade") String cidade,
            @Parameter(description = "Número da página (padrão 1)") int page,
            @Parameter(description = "Quantidade de itens por página (padrão 10)") int limit,
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado
    );

    @Operation(summary = "Buscar todos os pacientes", description = "Retorna uma lista paginada de todos os pacientes cadastrados, independente do profissional, podendo ser filtrada por nome, CPF e cidade.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pacientes retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    ResponseEntity<PaginatedResponseDTO<PacienteResponseDTO>> buscarTodosPacientes(
            @Parameter(description = "Filtro opcional por nome") String nome,
            @Parameter(description = "Filtro opcional por CPF") String cpf,
            @Parameter(description = "Filtro opcional por cidade") String cidade,
            @Parameter(description = "Número da página (padrão 1)") int page,
            @Parameter(description = "Quantidade de itens por página (padrão 10)") int limit
    );

    @Operation(summary = "Adicionar foto do paciente", description = "Realiza o upload de uma foto para o paciente informado, vinculado ao profissional autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Foto adicionada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Arquivo inválido"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado para o profissional autenticado")
    })
    ResponseEntity<String> adicionarFoto(
            @Parameter(description = "Arquivo de imagem a ser enviado") MultipartFile foto,
            @Parameter(description = "ID do paciente") UUID pacienteId,
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado
    );

    @Operation(summary = "Listar pacientes para dropdown", description = "Retorna uma lista simplificada de pacientes do profissional autenticado, para uso em componentes de seleção (dropdown).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    ResponseEntity<List<PacienteDropdownResponseDTO>> listarParaDropdown(
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado
    );
}