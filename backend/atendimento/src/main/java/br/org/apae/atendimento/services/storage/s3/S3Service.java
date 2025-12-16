package br.org.apae.atendimento.services.storage.s3;

import br.org.apae.atendimento.exceptions.MinioStorageException;
import br.org.apae.atendimento.services.storage.ObjectStorageService;
import br.org.apae.atendimento.services.storage.s3.S3PresignedUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;

@Service
@Profile("prod")
public class S3Service implements ObjectStorageService {

    private final S3Client client;

    private final S3PresignedUrlService urlService;

    @Value("${bucket.name}")
    private String BUCKET_NAME;

    @Autowired
    public S3Service(S3Client client, S3PresignedUrlService urlService) {
        this.client = client;
        this.urlService = urlService;
    }

    public String uploadArquivo(String objectName, MultipartFile file) {
        if (objectName == null || objectName.isBlank()) {
            throw new MinioStorageException("O nome do arquivo no MinIO não pode ser vazio.");
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

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(objectName)
                    .contentType(contentType)
                    .contentLength(file.getSize())
                    .build();

            client.putObject(request, RequestBody.fromInputStream(is, file.getSize()));

        } catch (Exception e) {
            throw new MinioStorageException("Erro ao enviar arquivo para o bucket", e);
        }
    }

    public void deletarArquivo(String objectName){
        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(objectName)
                    .build();

            client.deleteObject(deleteRequest);

        } catch (Exception e){
            e.printStackTrace();
            throw new MinioStorageException("Erro ao apagar arquivo", e);
        }
    }


}