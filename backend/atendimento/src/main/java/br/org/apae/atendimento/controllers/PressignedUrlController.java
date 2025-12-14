package br.org.apae.atendimento.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.org.apae.atendimento.services.PresignedUrlService;


@RestController
@RequestMapping("/pressigned")
public class PressignedUrlController {
    @Autowired
    PresignedUrlService presignedUrlService;
    public PressignedUrlController (PresignedUrlService presignedUrlService) {
        this.presignedUrlService = presignedUrlService;
    }


     @GetMapping("/{bucket}")
    public ResponseEntity<String> obterUrl (@PathVariable String bucket,
                                            @RequestParam(name = "objectName") String objectName,
                                            @RequestParam(name = "fileName") String fileName) {
        String url = presignedUrlService.gerarUrlPreAssinada(bucket, objectName, fileName);
        return ResponseEntity.ok(url);
    }


}