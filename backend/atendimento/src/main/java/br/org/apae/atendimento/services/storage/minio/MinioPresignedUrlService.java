package br.org.apae.atendimento.services.storage.minio;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Autowired
    public MinioPresignedUrlService(@Qualifier("minioPresignerClient") MinioClient client) {
        this.client = client;
    }

        @Override
    public String gerarUrlPreAssinada(String objectName) {
        return gerarUrlPreAssinada(objectName, null);
    }

    @Override
    @Cacheable(
            value = "presignedUrls",
            key = "'presigned:' + ':' + #objectName",
            unless = "#result == null"
    )
    public String gerarUrlPreAssinada(String objectName, String nomeArquivo) {
        try {
            java.util.Map<String, String> extraParams = new java.util.HashMap<>();

            String filename = (nomeArquivo != null && !nomeArquivo.isBlank())
                    ? nomeArquivo
                    : (objectName.contains("/") ? objectName.substring(objectName.lastIndexOf('/') + 1) : objectName);

            extraParams.put("response-content-disposition", "inline; filename=\"" + filename + "\"");

            if (filename.toLowerCase().endsWith(".pdf")) {
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
        // O corpo é vazio porque a remoção do cache é feita interceptada pela anotação @CacheEvict
    }
}
