package br.org.apae.atendimento.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import br.org.apae.atendimento.controllers.docs.ArquivoControllerDocs;
import br.org.apae.atendimento.dtos.request.ArquivoRequestDTO;
import br.org.apae.atendimento.dtos.response.ArquivoResponseDTO;
import br.org.apae.atendimento.exceptions.CloudStorageException;
import br.org.apae.atendimento.security.UsuarioAutenticado;
import br.org.apae.atendimento.services.ArquivoService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@RestController
@RequestMapping("/arquivo")
public class ArquivoController implements ArquivoControllerDocs {
    
    @Autowired
    private ArquivoService service;

    @Autowired
    private Validator validator;

    @Override
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArquivoResponseDTO> upload(
            @RequestPart("file") MultipartFile file,
            @RequestPart("metadata") String metadataJson,
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado
    ) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        ArquivoRequestDTO metadata;
        try {
            metadata = mapper.readValue(metadataJson, ArquivoRequestDTO.class);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "JSON de metadados inválido", e);
        }

        Set<ConstraintViolation<ArquivoRequestDTO>> violations = validator.validate(metadata);
        if (!violations.isEmpty()) {
            String errorMsg = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining("; "));
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Metadados inválidos: " + errorMsg);
        }

        try {
            ArquivoResponseDTO dto = service.salvar(file, metadata, usuarioAutenticado.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (CloudStorageException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao processar arquivos: " + e.getMessage(), e);
        }
    }

    @Override
    @GetMapping("/{pacienteId}/{tipoId}")
    public ResponseEntity<List<ArquivoResponseDTO>> findByTipoId(
            @PathVariable UUID pacienteId,
            @PathVariable Long tipoId,
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado
            ){
        List<ArquivoResponseDTO> anexos = service.listar(usuarioAutenticado.getId(), pacienteId, tipoId);
        return ResponseEntity.ok().body(anexos);
    }

    @Override
    @GetMapping("/date/{pacienteId}/{tipoId}/{data}")
    public ResponseEntity<List<ArquivoResponseDTO>> findByTipoIdAndDate(
            @PathVariable UUID pacienteId,
            @PathVariable Long tipoId,
            @PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate data,
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado
            ){
        List<ArquivoResponseDTO> anexos = service.buscarPorData(usuarioAutenticado.getId(), pacienteId, tipoId, data);
        return ResponseEntity.ok().body(anexos);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(
            @RequestParam(name = "objectName") String objectName,
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado
    ) {
        service.deletar(objectName, usuarioAutenticado.getId());
        return ResponseEntity.noContent().build();
    }
}