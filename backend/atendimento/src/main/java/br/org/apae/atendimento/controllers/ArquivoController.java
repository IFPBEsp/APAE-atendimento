package br.org.apae.atendimento.controllers;

import br.org.apae.atendimento.dtos.ArquivoDTO;
import br.org.apae.atendimento.entities.Arquivo;
import br.org.apae.atendimento.services.AnexoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/anexo")
public class ArquivoController {
    @Autowired
    private AnexoService service;

    @PostMapping("/{pacienteId}/{profissionalId}/{tipoId}")
    public ResponseEntity<ArquivoDTO> upload(@PathVariable UUID pacienteId,
                                          @PathVariable UUID profissionalId,
                                          @PathVariable  Long tipoId,
                                          @RequestParam("file") MultipartFile file,
                                          @RequestParam("data")LocalDate data){
        Arquivo anexo = service.salvar(pacienteId, profissionalId, file, data, tipoId);
        ArquivoDTO arquivoDTO = ArquivoDTO.toDTO(anexo);
        return ResponseEntity.status(HttpStatus.CREATED).body(arquivoDTO);
    }

    @GetMapping("/{pacienteId}/{profissionalId}/{tipoId}")
    public ResponseEntity<List<Arquivo>> list(@PathVariable UUID pacienteId,
                                              @PathVariable UUID profissionalId,
                                              @PathVariable Long tipoId){
        List<Arquivo> anexos = service.listar(profissionalId, pacienteId, tipoId);
        return ResponseEntity.ok().body(anexos);
    }

    @DeleteMapping("/delete/{bucket}/")
    public ResponseEntity<Void> delete(@PathVariable String bucket,
                                       @RequestParam(name = "objectName") String objectName){
        service.deletar(bucket, objectName);
        return ResponseEntity.noContent().build();
    }
}
