package br.org.apae.atendimento.rest;
import br.org.apae.atendimento.dtos.AtendimentoRequestDTO;
import br.org.apae.atendimento.dtos.response.AtendimentoResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(
        name="Atendimentos",
        description = "Operações CRUD para gestão de atendimentos"
)
public interface IAtendimentoControllerApi {
    @Operation(
            summary = "Salva dados de Atendimento",
            description = "Método para salvar dados de atendimento",
            operationId = "addAtendimento"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Atendimento criado com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AtendimentoResponseDTO.class)
            )
    )
    @PostMapping("/atendimentos")
    ResponseEntity<AtendimentoResponseDTO> criarAtendimento(
            @RequestBody AtendimentoRequestDTO atendimento
    );
    @GetMapping("/paciente/{id}")
    ResponseEntity<List<AtendimentoResponseDTO>> listarAtendimentosDoPaciente(
            @PathVariable Long id
    );
}
