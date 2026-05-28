package br.org.apae.atendimento.services.storage.minio;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.org.apae.atendimento.exceptions.CloudStorageException;
import br.org.apae.atendimento.services.storage.PresignedUrlService;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;

@Service
@Profile({"test", "dev"})
public class MinioPresignedUrlService implements PresignedUrlService {
    private final MinioClient client;
    @Value("${bucket.name}")
    private String BUCKET_NAME;
    public MinioPresignedUrlService(MinioClient client) {
        this.client = client;
    }

    @Cacheable(
            value = "presignedUrls",
            key = "'presigned:' + ':' + #objectName", 
            unless = "#result == null"
    )
    public String gerarUrlPreAssinada(String objectName) {
        try {
            java.util.Map<String, String> extraParams = new java.util.HashMap<>();
            extraParams.put("response-content-disposition", "inline; filename=\"" + objectName + "\"");
            
            if (objectName.toLowerCase().endsWith(".pdf")) {
                extraParams.put("response-content-type", "application/pdf");
            }

            String url = client.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(objectName)
                            .extraQueryParams(extraParams) 
                            .method(io.minio.http.Method.GET)
                            .expiry(60 * 60)
                            .build()
            );

            return url;

        } catch (Exception e) {
            throw new CloudStorageException("Erro ao gerar URL do arquivo.", e);
        }
    }

    @CacheEvict(value = "presignedUrls", key = "'presigned:' + ':' + #objectName")
    public void evictUrlFromCache(String objectName) {
    }
}
