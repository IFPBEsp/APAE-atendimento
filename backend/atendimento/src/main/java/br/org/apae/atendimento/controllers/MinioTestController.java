package br.org.apae.atendimento.controllers;

import br.org.apae.atendimento.services.MinioService;
import io.minio.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.path;

@RestController
@RequestMapping("/files")
public class MinioTestController {
    @Autowired
    private MinioService service;

    // ==========================================
    //                 UPLOAD
    // ==========================================
    @PostMapping("/upload/{bucket}/{path}")
    public ResponseEntity<String> uploadFile(
            @PathVariable String bucket,
            @PathVariable String path,
            @RequestParam("file") MultipartFile file) {

        String objectName = UUID.randomUUID().toString();

        service.uploadArquivo(bucket, file, path, objectName);
        return ResponseEntity.ok().body("Upload feito");

    }

    // ==========================================
    //                 DOWNLOAD
    // ==========================================
    @GetMapping("/{bucket}/{path}/{objectName}")
    public ResponseEntity<InputStreamResource> downloadFile(
            @PathVariable String bucket,
            @PathVariable String path,
            @PathVariable String objectName) {

        return service.exibir(bucket, path, objectName);
    }

//    @GetMapping("/list/{bucket}/{path}")
//    public ResponseEntity<Base64DTO> list()
}
