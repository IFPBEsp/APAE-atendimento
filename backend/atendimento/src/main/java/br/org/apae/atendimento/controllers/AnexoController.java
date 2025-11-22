package br.org.apae.atendimento.controllers;

import br.org.apae.atendimento.dtos.ArquivoDTO;
import br.org.apae.atendimento.entities.Anexo;
import br.org.apae.atendimento.services.AnexoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/anexo")
public class AnexoController {
    @Autowired
    private AnexoService service;

    @PostMapping("/{pacienteId}/{profissionalId}")
    public ResponseEntity<ArquivoDTO> upload(@RequestPart("file") MultipartFile file,
                                             @RequestPart("metadata") ArquivoDTO metadata){
        ArquivoDTO anexoDTO = service.salvar(file, metadata);
        return ResponseEntity.status(HttpStatus.CREATED).body(anexoDTO);
    }

    @GetMapping("/{pacienteId}/{profissionalId}")
    public ResponseEntity<List<ArquivoDTO>> list(@PathVariable UUID pacienteId, @PathVariable Long profissionalId){
        List<ArquivoDTO> anexos = service.listarPorProfissionalEPaciente(profissionalId, pacienteId);
        return ResponseEntity.ok().body(anexos);
    }

    @DeleteMapping("/delete/{bucket}/")
    public ResponseEntity<Void> delete(@PathVariable String bucket,
                                       @RequestParam(name = "objectName") String objectName){
        service.deletar(bucket, objectName);
        return ResponseEntity.noContent().build();
    }
}
