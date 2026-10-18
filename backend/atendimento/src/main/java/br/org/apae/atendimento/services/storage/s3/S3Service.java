package br.org.apae.atendimento.services.storage.s3;

import br.org.apae.atendimento.exceptions.CloudStorageException;
import br.org.apae.atendimento.services.storage.ObjectStorageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Slf4j
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

    @PostConstruct
    public void inicializarBucket() {
        if (BUCKET_NAME == null || BUCKET_NAME.isBlank()) {
            log.warn("Variável 'bucket.name' (BUCKET_NAME) não está definida; verificação do bucket S3 ignorada.");
            return;
        }

        try {
            client.headBucket(HeadBucketRequest.builder().bucket(BUCKET_NAME).build());
            log.info("Bucket '{}' validado com sucesso no armazenamento S3.", BUCKET_NAME);
        } catch (NoSuchBucketException e) {
            criarBucketSeAusente();
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                criarBucketSeAusente();
            } else if (e.statusCode() == 403 || e.statusCode() == 401) {
                log.error("Falha de autenticação ou permissão ao validar o bucket '{}' no S3 (HTTP {}). Verifique as credenciais configuradas.",
                        BUCKET_NAME, e.statusCode(), e);
            } else {
                log.error("Erro ao validar o bucket '{}' no S3 (HTTP {}): {}",
                        BUCKET_NAME, e.statusCode(), e.getMessage(), e);
            }
        } catch (SdkClientException e) {
            log.warn("Serviço de armazenamento S3 indisponível durante a inicialização ao verificar o bucket '{}': {}",
                    BUCKET_NAME, e.getMessage());
        } catch (Exception e) {
            log.warn("Não foi possível validar o bucket '{}' durante a inicialização: {}",
                    BUCKET_NAME, e.getMessage(), e);
        }
    }

    private void criarBucketSeAusente() {
        try {
            client.createBucket(CreateBucketRequest.builder().bucket(BUCKET_NAME).build());
            log.info("Bucket '{}' não existia e foi criado automaticamente no armazenamento S3.", BUCKET_NAME);
        } catch (Exception createEx) {
            log.warn("Bucket '{}' não encontrado e não pôde ser criado automaticamente no S3: {}",
                    BUCKET_NAME, createEx.getMessage());
        }
    }

    public String uploadArquivo(String objectName, MultipartFile file) {
        if (objectName == null || objectName.isBlank()) {
            throw new CloudStorageException("O nome do arquivo na nuvem não pode ser vazio.");
        }
        if (file == null || file.isEmpty()) {
            throw new CloudStorageException("O arquivo selecionado está vazio ou é inválido.");
        }

        colocarArquivo(objectName, file);
        return urlService.gerarUrlPreAssinada(objectName);
    }

    public void evictUrlFromCache(String objectName) {
        urlService.evictUrlFromCache(objectName);
    }

    private void colocarArquivo(String objectName, MultipartFile file) {
        try {
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

            client.putObject(request, RequestBody.fromBytes(file.getBytes()));

        } catch (NoSuchBucketException e) {
            log.error("Bucket '{}' não existe no armazenamento em nuvem ao tentar enviar '{}'.", BUCKET_NAME, objectName, e);
            throw new CloudStorageException("Erro ao enviar arquivo para o armazenamento em nuvem.", e);
        } catch (S3Exception e) {
            log.error("Erro S3 (HTTP {}) ao enviar arquivo '{}' para o bucket '{}': {}",
                    e.statusCode(), objectName, BUCKET_NAME, e.getMessage(), e);
            throw new CloudStorageException("Erro ao enviar arquivo para o armazenamento em nuvem.", e);
        } catch (Exception e) {
            log.error("Falha de comunicação com o armazenamento em nuvem ao enviar '{}': {}", objectName, e.getMessage(), e);
            throw new CloudStorageException("Erro ao enviar arquivo para o armazenamento em nuvem.", e);
        }
    }

    public void deletarArquivo(String objectName) {
        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(objectName)
                    .build();

            client.deleteObject(deleteRequest);

        } catch (Exception e) {
            log.error("Erro de comunicação ao tentar apagar o arquivo '{}' no bucket '{}': {}",
                    objectName, BUCKET_NAME, e.getMessage(), e);
            throw new CloudStorageException("Erro de comunicação ao tentar apagar o arquivo na nuvem.", e);
        }
    }
}