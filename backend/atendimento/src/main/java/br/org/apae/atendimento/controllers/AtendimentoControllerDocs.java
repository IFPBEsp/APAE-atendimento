package br.org.apae.atendimento.controllers;

import br.org.apae.atendimento.dtos.request.AtendimentoRequestDTO;
import br.org.apae.atendimento.dtos.response.AtendimentoResponseDTO;
import br.org.apae.atendimento.dtos.response.MesAnoAtendimentoResponseDTO;
import br.org.apae.atendimento.security.UsuarioAutenticado;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@Tag(name = "Atendimento", description = "Endpoints para registro, consulta e gerenciamento de atendimentos")
public interface AtendimentoControllerDocs {

    @Operation(summary = "Criar atendimento", description = "Registra um novo atendimento com seus tópicos e relatórios.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Atendimento criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    ResponseEntity<AtendimentoResponseDTO> criarAtendimento(
            @Valid @RequestBody AtendimentoRequestDTO atendimento,
            @Parameter(hidden = true) UsuarioAutenticado usuarioAutenticado
    );

    @Operation(summary = "Listar atendimentos do paciente", description = "Retorna a lista de atendimentos de um paciente específico agrupados por mês e ano.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de atendimentos recuperada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado")
    })
    ResponseEntity<List<MesAnoAtendimentoResponseDTO>> listarAtendimentosDoPaciente(
            @Parameter(description = "ID do paciente")
            @PathVariable UUID pacienteId,
            @Parameter(hidden = true) UsuarioAutenticado usuarioAutenticado
    );

    @Operation(summary = "Excluir atendimento", description = "Remove um atendimento específico do paciente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Atendimento excluído com sucesso"),
            @ApiResponse(responseCode = "400", description = "Sem permissão para excluir atendimento"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Atendimento não encontrado")
    })
    ResponseEntity<String> deletar(
            @Parameter(description = "ID do paciente")
            @PathVariable UUID pacienteId,
            @Parameter(description = "ID do atendimento a ser excluído")
            @PathVariable UUID atendimentoId,
            @Parameter(hidden = true) UsuarioAutenticado usuarioAutenticado
    );

    @Operation(summary = "Editar atendimento", description = "Atualiza os tópicos e dados de um atendimento existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Atendimento atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou sem permissão para editar"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Atendimento não encontrado")
    })
    ResponseEntity<AtendimentoResponseDTO> editarTopicos(
            @Valid @RequestBody AtendimentoRequestDTO updateDTO,
            @Parameter(description = "ID do atendimento a ser editado")
            @PathVariable UUID atendimentoId,
            @Parameter(hidden = true) UsuarioAutenticado usuarioAutenticado
    );

    @Operation(summary = "Concluir atendimento", description = "Finaliza o atendimento e marca seu status como concluído.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Atendimento concluído com sucesso"),
            @ApiResponse(responseCode = "400", description = "Sem permissão ou atendimento já concluído"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Atendimento não encontrado")
    })
    ResponseEntity<String> concluirAtendimento(
            @Parameter(description = "ID do atendimento a ser concluído")
            @PathVariable UUID atendimentoId,
            @Parameter(hidden = true) UsuarioAutenticado usuarioAutenticado
    );
}
