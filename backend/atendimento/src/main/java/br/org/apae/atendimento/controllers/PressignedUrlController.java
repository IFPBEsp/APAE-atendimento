package br.org.apae.atendimento.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.org.apae.atendimento.services.storage.PresignedUrlService;


@RestController
@RequestMapping("/pressigned")
public class PressignedUrlController {
    @Autowired
    private PresignedUrlService presignedUrlService;

    @Value("${bucket.name}")
    private String BUCKET_NAME;

    public PressignedUrlController (PresignedUrlService presignedUrlService) {
        this.presignedUrlService = presignedUrlService;
    }


    @GetMapping
    public ResponseEntity<String> obterUrl (@RequestParam(name = "objectName") String objectName) {
        String url = presignedUrlService.gerarUrlPreAssinada(objectName);
        return ResponseEntity.ok(url);
    }


}