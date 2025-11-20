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

    @Lazy
    @Autowired
    private MinioService self;

    private static final Duration PRESIGNED_TTL = Duration.ofDays(7);
    private static final Duration MARGEM_RENOVAR = Duration.ofHours(1);

    @Autowired
    public MinioService(MinioClient client) {
        this.client = client;
    }

    @Cacheable(
            value = "presignedUrls",
            key = "'presigned:' + #bucket + ':' + #objectName",  // ← CHAVE EXPLÍCITA
            unless = "#result == null"
    )
    public String gerarUrlPreAssinada(String bucket, String objectName) {
        try {
            System.out.println("🔴 CACHE MISS - Gerando NOVA URL: " + bucket + "/" + objectName);
            System.out.println("   Chave: presigned:" + bucket + ":" + objectName);

            String url = client.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .method(io.minio.http.Method.GET)
                            .expiry(60 * 60)
                            .build()
            );

            System.out.println("✅ URL gerada (tamanho: " + url.length() + ")");
            return url;

        } catch (Exception e) {
            throw new MinioStorageException("Erro ao gerar URL", e);
        }
    }

    @CacheEvict(value = "presignedUrls", key = "'presigned:' + #bucket + ':' + #objectName")
    public void evictUrlFromCache(String bucket, String objectName) {
        System.out.println("🗑️ Removendo do cache: presigned:" + bucket + ":" + objectName);
    }

    public String uploadArquivo(String bucket, String objectName, MultipartFile file) {
        if (objectName == null || objectName.isBlank()) {
            throw new MinioStorageException("O nome do arquivo no MinIO não pode ser vazio.");
        }

        try {
            criarBucketSeNaoExiste(bucket);
            colocarArquivo(bucket, objectName, file);
            return self.gerarUrlPreAssinada(bucket, objectName);

        } catch (Exception e) {
            throw new MinioStorageException("Erro ao fazer upload no MinIO.", e);
        }
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
}