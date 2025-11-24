package br.org.apae.atendimento.controllers;

import br.org.apae.atendimento.dtos.request.ArquivoRequestDTO;
import br.org.apae.atendimento.dtos.response.ArquivoResponseDTO;
import br.org.apae.atendimento.services.ArquivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/arquivo")
public class AnexoController {
    @Autowired
    private ArquivoService service;

    @PostMapping
    public ResponseEntity<ArquivoResponseDTO> upload(@RequestPart("file") MultipartFile file,
                                                     @RequestPart("metadata") ArquivoRequestDTO arquivoRequest){
        ArquivoResponseDTO anexoDTO = service.salvar(file, arquivoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(anexoDTO);
    }

    @GetMapping("/list")
    public ResponseEntity<List<ArquivoResponseDTO>> list(@RequestBody ArquivoRequestDTO arquivoRequest){
        List<ArquivoResponseDTO> anexos = service.listar(arquivoRequest);
        return ResponseEntity.ok().body(anexos);
    }

    @GetMapping("/list/date")
    public ResponseEntity<List<ArquivoResponseDTO>> listByDate(@RequestBody ArquivoRequestDTO arquivoRequest,
                                                               @RequestParam(name = "data") LocalDate data){
        List<ArquivoResponseDTO> anexos = service.buscarPorData(arquivoRequest, data);
        return ResponseEntity.ok().body(anexos);
    }

    @DeleteMapping("/delete/{bucket}/")
    public ResponseEntity<Void> delete(@PathVariable String bucket,
                                       @RequestParam(name = "objectName") String objectName){
        service.deletar(bucket, objectName);
        return ResponseEntity.noContent().build();
    }
}
