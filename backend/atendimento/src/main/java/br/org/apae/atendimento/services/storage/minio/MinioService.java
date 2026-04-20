package br.org.apae.atendimento.services.storage.minio;

import br.org.apae.atendimento.exceptions.CloudStorageException;
import br.org.apae.atendimento.services.storage.ObjectStorageService;
import br.org.apae.atendimento.services.storage.PresignedUrlService;
import io.minio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
@Profile({"test", "dev"})
public class MinioService implements ObjectStorageService {

    private final MinioClient client;

    private final PresignedUrlService urlService;

    @Value("${bucket.name}")
    private String BUCKET_NAME;

    @Autowired
    public MinioService(MinioClient client, PresignedUrlService urlService) {
        this.client = client;
        this.urlService = urlService;
    }

    public String uploadArquivo(String objectName, MultipartFile file) {
        if (objectName == null || objectName.isBlank()) {
            throw new CloudStorageException("O nome do arquivo no MinIO não pode ser vazio.");
        }

        if (file == null || file.isEmpty()) {
            throw new CloudStorageException("O arquivo selecionado está vazio ou inválido.");
        }

        colocarArquivo(objectName, file);
        return urlService.gerarUrlPreAssinada(objectName);
    }

    public void evictUrlFromCache(String objectName) {
        urlService.evictUrlFromCache(objectName);
    }

    private void colocarArquivo(String objectName, MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            String contentType = file.getContentType();
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            client.putObject(
                    PutObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(objectName)
                            .stream(is, file.getSize(), -1)
                            .contentType(contentType)
                            .build()
            );
        } catch (Exception e) {
            throw new CloudStorageException("Erro ao enviar arquivo para a nuvem. Tente novamente.", e);
        }
    }

    public void deletarArquivo(String objectName) {
        try {
            client.removeObject(RemoveObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            throw new CloudStorageException("Erro de comunicação ao tentar apagar o arquivo.", e);
        }
    }
}
