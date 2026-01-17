package br.org.apae.atendimento.services.storage.s3;

import br.org.apae.atendimento.exceptions.MinioStorageException;
import br.org.apae.atendimento.services.storage.PresignedUrlService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;

@Service
@Profile("prod")
public class S3PresignedUrlService implements PresignedUrlService {
    private final S3Presigner s3Presigner;
    @Value("${bucket.name}")
    private String BUCKET_NAME;
    public S3PresignedUrlService(S3Presigner s3Presigner) {
        this.s3Presigner = s3Presigner;
    }

    @Cacheable(
            value = "presignedUrls",
            key = "'presigned:' + #bucket + ':' + #objectName",  // ← CHAVE EXPLÍCITA
            unless = "#result == null"
    )
    public String gerarUrlPreAssinada(String objectName) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(objectName)
                    .responseContentDisposition(
        "attachment; filename=\"" + objectName + "\""
    )
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .getObjectRequest(getObjectRequest)
                    .signatureDuration(Duration.ofHours(1))
                    .build();

            return s3Presigner.presignGetObject(presignRequest).url().toString();
        } catch (Exception e) {
            throw new MinioStorageException("Erro ao gerar URL", e);
        }
    }

    @CacheEvict(value = "presignedUrls", key = "'presigned:' + #bucket + ':' + #objectName")
    public void evictUrlFromCache(String objectName) {
        System.out.println("🗑️ Removendo do cache: presigned:" + BUCKET_NAME + ":" + objectName);
    }
}
