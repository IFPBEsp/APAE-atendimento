package br.org.apae.atendimento.controllers.docs;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import br.org.apae.atendimento.dtos.request.ArquivoRequestDTO;
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

    @Schema(description = "Formulário para upload de arquivo e seus metadados")
    record UploadArquivoForm(
            @Schema(description = "Arquivo binário a ser enviado", type = "string", format = "binary", requiredMode = Schema.RequiredMode.REQUIRED)
            MultipartFile file,

            @Schema(description = "Metadados do arquivo", requiredMode = Schema.RequiredMode.REQUIRED)
            ArquivoRequestDTO metadata
    ) {}

    @Operation(
            summary = "Realizar upload de arquivo",
            description = "Envia um arquivo binário junto com a estrutura de metadados necessários.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = UploadArquivoForm.class)
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Arquivo salvo com sucesso"),
            @ApiResponse(responseCode = "400", description = "JSON de metadados inválido ou erro no storage"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Paciente ou tipo de arquivo não encontrado"),
            @ApiResponse(responseCode = "413", description = "Tamanho do arquivo excede o limite máximo permitido")
    })
    ResponseEntity<ArquivoResponseDTO> upload(
            @Parameter(description = "Arquivo a ser enviado", required = true) MultipartFile file,
            @Parameter(description = "JSON serializado contendo os metadados do arquivo (ArquivoRequestDTO)", required = true) String metadataJson,
            @Parameter(hidden = true) UsuarioAutenticado usuarioAutenticado
    );

    @Operation(summary = "Buscar arquivos por Tipo e Paciente", description = "Retorna uma lista de arquivos filtrada pelo ID do paciente e pelo tipo de documento.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Arquivos encontrados e listados"),
            @ApiResponse(responseCode = "400", description = "Parâmetros de busca inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Paciente ou tipo de documento não encontrado")
    })
    ResponseEntity<List<ArquivoResponseDTO>> findByTipoId(
            @Parameter(description = "ID do paciente (UUID)", required = true) UUID pacienteId,
            @Parameter(description = "ID do tipo de arquivo", required = true) Long tipoId,
            @Parameter(hidden = true) UsuarioAutenticado usuarioAutenticado
    );

    @Operation(summary = "Buscar arquivos por Tipo, Paciente e Data", description = "Retorna uma lista de arquivos filtrada pelo ID do paciente, tipo de documento e data específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Arquivos encontrados e listados"),
            @ApiResponse(responseCode = "400", description = "Formato de data ou parâmetros inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Paciente ou arquivos não encontrados para os filtros fornecidos")
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
            @ApiResponse(responseCode = "400", description = "Parâmetro objectName inválido ou ausente"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Arquivo não encontrado para exclusão")
    })
    ResponseEntity<Void> delete(
            @Parameter(description = "Nome do objeto (objectName) armazenado no storage", required = true) String objectName,
            @Parameter(hidden = true) UsuarioAutenticado usuarioAutenticado
    );
}