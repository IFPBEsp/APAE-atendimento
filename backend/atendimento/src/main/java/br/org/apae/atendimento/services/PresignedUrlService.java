package br.org.apae.atendimento.services;

import br.org.apae.atendimento.exceptions.MinioStorageException;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.messages.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class PresignedUrlService {

    private MinioClient client;

    @Value("${bucket.name}")
    private String BUCKET_NAME;
    public PresignedUrlService(MinioClient minioClient) {
        this.client = minioClient;
    }

    @Cacheable(
            value = "presignedUrls",
            key = "'presigned:' + #bucket + ':' + #objectName",  // ← CHAVE EXPLÍCITA
            unless = "#result == null"
    )
    public String gerarUrlPreAssinada(String objectName) {
        try {
            System.out.println("🔴 CACHE MISS - Gerando NOVA URL: " + BUCKET_NAME + "/" + objectName);
            System.out.println("   Chave: presigned:" + BUCKET_NAME + ":" + objectName);
            String url = client.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(BUCKET_NAME)
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
    public void evictUrlFromCache(String objectName) {
        System.out.println("🗑️ Removendo do cache: presigned:" + BUCKET_NAME + ":" + objectName);
    }
}
