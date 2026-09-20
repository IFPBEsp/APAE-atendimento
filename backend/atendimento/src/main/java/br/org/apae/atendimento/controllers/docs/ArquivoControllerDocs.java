package br.org.apae.atendimento.controllers.docs;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import br.org.apae.atendimento.dtos.response.ArquivoResponseDTO;
import br.org.apae.atendimento.security.UsuarioAutenticado;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Arquivo", description = "Endpoints para upload, listagem e exclusão de arquivos e anexos de pacientes no MinIO")
public interface ArquivoControllerDocs {

    @Operation(
            summary = "Realizar upload de arquivo",
            description = "Envia um arquivo binário acompanhado de uma string JSON com os metadados (ArquivoRequestDTO).",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = MultipartFile.class)
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Arquivo salvo com sucesso"),
            @ApiResponse(responseCode = "400", description = "JSON de metadados inválido ou erro no storage"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    ResponseEntity<ArquivoResponseDTO> upload(
            @Parameter(description = "Arquivo a ser enviado", required = true) MultipartFile file,
            @Parameter(description = "JSON serializado contendo os metadados do arquivo (ArquivoRequestDTO)", required = true) String metadataJson,
            @Parameter(hidden = true) UsuarioAutenticado usuarioAutenticado
    );

    @Operation(summary = "Buscar arquivos por Tipo e Paciente", description = "Retorna uma lista de arquivos filtrada pelo ID do paciente e pelo tipo de documento.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Arquivos encontrados e listados"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    ResponseEntity<List<ArquivoResponseDTO>> findByTipoId(
            @Parameter(description = "ID do paciente (UUID)", required = true) UUID pacienteId,
            @Parameter(description = "ID do tipo de arquivo", required = true) Long tipoId,
            @Parameter(hidden = true) UsuarioAutenticado usuarioAutenticado
    );

    @Operation(summary = "Buscar arquivos por Tipo, Paciente e Data", description = "Retorna uma lista de arquivos filtrada pelo ID do paciente, tipo de documento e data específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Arquivos encontrados e listados"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    ResponseEntity<List<ArquivoResponseDTO>> findByTipoIdAndDate(
            @Parameter(description = "ID do paciente (UUID)", required = true) UUID pacienteId,
            @Parameter(description = "ID do tipo de arquivo", required = true) Long tipoId,
            @Parameter(description = "Data no formato dd-MM-yyyy", required = true) LocalDate data,
            @Parameter(hidden = true) UsuarioAutenticado usuarioAutenticado
    );

    @Operation(summary = "Deletar arquivo", description = "Exclui um arquivo do MinIO e do banco de dados utilizando seu nome de objeto único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Arquivo deletado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    ResponseEntity<Void> delete(
            @Parameter(description = "Nome do objeto (objectName) armazenado no storage", required = true) String objectName,
            @Parameter(hidden = true) UsuarioAutenticado usuarioAutenticado
    );
}