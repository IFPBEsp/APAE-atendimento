package br.org.apae.atendimento.services;


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

    private String gerarUrl (String bucket, String objectName, Map<String,String> queryParams){
         try {
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

     @Cacheable(
        value = "presignedUrls",
        key = "'presigned:' + #bucket + ':' + #objectName + ':' + #fileName",
        unless = "#result == null"
)
      public String gerarUrlPreAssinada(String bucket, String objectName, String fileName) {
        Map<String, String> params = Map.of(
                "response-content-disposition",
                "attachment; filename=\"" + fileName + "\""
        );
        return gerarUrl(bucket, objectName, params);
    }


    @Cacheable(
            value = "presignedUrls",
            key = "'presigned:' + #bucket + ':' + #objectName",  
            unless = "#result == null"
    )
    public String gerarUrlPreAssinada(String bucket, String objectName) {
        try {
            return gerarUrl(bucket, objectName, Map.of());
        } catch (Exception e) {
            throw new MinioStorageException("Erro ao gerar URL", e);
        }
    }

    @CacheEvict(value = "presignedUrls", key = "'presigned:' + #bucket + ':' + #objectName")
    public void evictUrlFromCache(String bucket, String objectName) {
        System.out.println("🗑️ Removendo do cache: presigned:" + bucket + ":" + objectName);
    }
}
