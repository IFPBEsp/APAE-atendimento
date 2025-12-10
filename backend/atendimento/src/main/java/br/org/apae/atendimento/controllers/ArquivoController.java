package br.org.apae.atendimento.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArquivoResponseDTO> upload(@RequestPart("file") MultipartFile file,
                                                     @RequestPart("metadata") ArquivoRequestDTO arquivoRequest){
        ArquivoResponseDTO anexoDTO = service.salvar(file, arquivoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(anexoDTO);
    }

    @GetMapping("/list/{profissionalId}/{pacienteId}/{tipoId}")
    public ResponseEntity<List<ArquivoResponseDTO>> list(
            @PathVariable UUID profissionalId,
            @PathVariable UUID pacienteId,
            @PathVariable Long tipoId
    ){
        List<ArquivoResponseDTO> anexos = service.listar(profissionalId, pacienteId, tipoId);
        return ResponseEntity.ok().body(anexos);
    }

    @GetMapping("/list/date/{profissionalId}/{pacienteId}/{tipoId}/{data}")
    public ResponseEntity<List<ArquivoResponseDTO>> listByDate(
            @PathVariable UUID profissionalId,
            @PathVariable UUID pacienteId,
            @PathVariable Long tipoId,
            @PathVariable LocalDate data
            ){
        List<ArquivoResponseDTO> anexos = service.buscarPorData(profissionalId, pacienteId, tipoId, data);
        return ResponseEntity.ok().body(anexos);
    }

    @DeleteMapping("/delete/{bucket}")
    public ResponseEntity<Void> delete(@PathVariable String bucket,
                                       @RequestParam(name = "objectName") String objectName){
        service.deletar(bucket, objectName);
        return ResponseEntity.noContent().build();
    }
    
}
