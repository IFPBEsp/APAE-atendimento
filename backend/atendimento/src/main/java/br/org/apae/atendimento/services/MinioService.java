package br.org.apae.atendimento.services;

import br.org.apae.atendimento.exceptions.MinioStorageException;
import io.minio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.Duration;


@Service
public class MinioService {

    private final MinioClient client;

    private final PresignedUrlService urlService;

    @Autowired
    public MinioService(MinioClient client, PresignedUrlService urlService) {
        this.client = client;
        this.urlService = urlService;
    }

    public String uploadArquivo(String bucket, String objectName, MultipartFile file) {
        if (objectName == null || objectName.isBlank()) {
            throw new MinioStorageException("O nome do arquivo no MinIO não pode ser vazio.");
        }

        try {
            criarBucketSeNaoExiste(bucket);
            colocarArquivo(bucket, objectName, file);
            return urlService.gerarUrlPreAssinada(bucket, objectName);

        } catch (Exception e) {
            e.printStackTrace();
            throw new MinioStorageException("Erro ao fazer upload no MinIO.", e);
        }
    }

    public void evictUrlFromCache(String bucket, String objectName) {
        urlService.evictUrlFromCache(bucket, objectName);
    }

    private void criarBucketSeNaoExiste(String bucket) {
        try {
            boolean exists = client.bucketExists(
                    BucketExistsArgs.builder().bucket(bucket).build()
            );
            if (!exists) {
                client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }
        } catch (Exception e) {
            throw new MinioStorageException("Erro ao verificar/criar bucket '" + bucket + "'", e);
        }
    }

    private void colocarArquivo(String bucket, String objectName, MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            String contentType = file.getContentType();
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            client.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .stream(is, file.getSize(), -1)
                            .contentType(contentType)
                            .build()
            );
        } catch (Exception e) {
            throw new MinioStorageException("Erro ao enviar arquivo para o bucket", e);
        }
    }

    public void deletarArquivo(String bucket, String objectName){
        try {
            client.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build());
        } catch (Exception e){
            e.printStackTrace();
            throw new MinioStorageException("Erro ao apagar arquivo", e);
        }
    }
}