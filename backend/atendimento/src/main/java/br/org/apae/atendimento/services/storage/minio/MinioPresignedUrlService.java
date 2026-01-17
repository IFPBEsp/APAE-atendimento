package br.org.apae.atendimento.services.storage.minio;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.org.apae.atendimento.exceptions.MinioStorageException;
import br.org.apae.atendimento.services.storage.PresignedUrlService;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;

@Service
@Profile("test")
public class MinioPresignedUrlService implements PresignedUrlService {
    private final MinioClient client;
    @Value("${bucket.name}")
    private String BUCKET_NAME;
    public MinioPresignedUrlService(MinioClient client) {
        this.client = client;
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
                            .extraQueryParams(
            Map.of(
                "response-content-disposition",
                "attachment; filename=" + objectName
            )
        )
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
