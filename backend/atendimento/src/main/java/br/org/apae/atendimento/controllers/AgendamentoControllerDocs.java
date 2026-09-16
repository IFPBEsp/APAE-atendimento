package br.org.apae.atendimento.controllers;

import br.org.apae.atendimento.dtos.request.AgendamentoRequestDTO;
import br.org.apae.atendimento.dtos.response.AgendamentoResponseDTO;
import br.org.apae.atendimento.dtos.response.DiaAgendamentoResponseDTO;
import br.org.apae.atendimento.security.UsuarioAutenticado;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.UUID;

@Tag(name = "Agendamento", description = "Endpoints para gerenciamento de agendamentos e horários")
public interface AgendamentoControllerDocs {

    @Operation(summary = "Criar agendamento", description = "Registra um novo agendamento para o profissional autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Agendamento criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou conflito de horário"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    ResponseEntity<AgendamentoResponseDTO> agendarPaciente(
            AgendamentoRequestDTO agendamentoRequest,
            @Parameter(hidden = true) UsuarioAutenticado usuarioAutenticado
    );

    @Operation(summary = "Editar agendamento", description = "Atualiza as informações de um agendamento existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamento atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou agendamento já concluído"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    })
    ResponseEntity<AgendamentoResponseDTO> editarAgendamento(
            @Parameter(description = "ID do agendamento a ser editado")
            UUID agendamentoId,
            AgendamentoRequestDTO agendamentoRequest,
            @Parameter(hidden = true) UsuarioAutenticado usuarioAutenticado
    );

    @Operation(summary = "Listar agendamentos agrupados por dia", description = "Retorna os agendamentos do profissional autenticado agrupados por data, com suporte a paginação e filtro por dia.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamentos listados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    ResponseEntity<Page<DiaAgendamentoResponseDTO>> listarAgendamentoAgrupadoPorDia(
            @Parameter(description = "Data para filtro dos agendamentos", example = "2026-09-16")
            LocalDate data,
            @Parameter(description = "Número da página (iniciando em 0)", example = "0")
            int page,
            @Parameter(description = "Quantidade de agendamentos por página", example = "10")
            int size,
            @Parameter(hidden = true) UsuarioAutenticado usuarioAutenticado
    );

    @Operation(summary = "Excluir agendamento", description = "Remove um agendamento existente do profissional autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamento excluído com sucesso"),
            @ApiResponse(responseCode = "400", description = "Sem vínculo com o paciente"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    })
    ResponseEntity<String> deletarAgendamento(
            @Parameter(description = "ID do paciente")
            UUID pacienteId,
            @Parameter(description = "ID do agendamento a ser excluído")
            UUID agendamentoId,
            @Parameter(hidden = true) UsuarioAutenticado usuarioAutenticado
    );

    @Operation(summary = "Concluir agendamento", description = "Marca o status do agendamento como concluído.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamento concluído com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    })
    ResponseEntity<String> concluirAgendamento(
            @Parameter(description = "ID do paciente")
            UUID pacienteId,
            @Parameter(description = "ID do agendamento a ser concluído")
            UUID agendamentoId,
            @Parameter(hidden = true) UsuarioAutenticado usuarioAutenticado
    );
}
