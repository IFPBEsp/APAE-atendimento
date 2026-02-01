package br.org.apae.atendimento.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import br.org.apae.atendimento.security.UsuarioAutenticado;
import org.checkerframework.checker.units.qual.A;
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

import br.org.apae.atendimento.dtos.request.ArquivoRequestDTO;
import br.org.apae.atendimento.dtos.response.ArquivoResponseDTO;
import br.org.apae.atendimento.services.ArquivoService;

@RestController
@RequestMapping("/arquivo")
public class ArquivoController {
    @Autowired
    private ArquivoService service;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArquivoResponseDTO> upload(@RequestPart("file") MultipartFile file,
                                                     @RequestPart("metadata") ArquivoRequestDTO arquivoRequest,
                                                     @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado){
        ArquivoResponseDTO anexoDTO = service.salvar(file, arquivoRequest, usuarioAutenticado.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(anexoDTO);
    }

    @GetMapping("/{pacienteId}/{tipoId}")
    public ResponseEntity<List<ArquivoResponseDTO>> findByTipoId(
            @PathVariable UUID pacienteId,
            @PathVariable Long tipoId,
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado
            ){
        List<ArquivoResponseDTO> anexos = service.listar(usuarioAutenticado.getId(), pacienteId, tipoId);
        return ResponseEntity.ok().body(anexos);
    }

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

    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam(name = "objectName") String objectName){
        service.deletar(objectName);
        return ResponseEntity.noContent().build();
    }
}
