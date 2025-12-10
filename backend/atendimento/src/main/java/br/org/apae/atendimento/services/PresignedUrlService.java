package br.org.apae.atendimento.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import br.org.apae.atendimento.exceptions.MinioStorageException;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;

@Service
public class PresignedUrlService {

    private MinioClient client;

    public PresignedUrlService(MinioClient minioClient) {
        this.client = minioClient;
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
            Map<String, String> queryParams = new HashMap<>();
queryParams.put("response-content-disposition", "attachment");
queryParams.put("response-content-type", "application/octet-stream");
            String url = client.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .method(io.minio.http.Method.GET)
                            .extraQueryParams(queryParams)
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
}
