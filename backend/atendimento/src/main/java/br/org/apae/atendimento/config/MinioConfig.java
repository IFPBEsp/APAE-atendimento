package br.org.apae.atendimento.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.net.ConnectException;

@Slf4j
@Configuration
@Profile({"test", "dev"})
public class MinioConfig {

    @Value("${minio.endpoint}")
    private String minioEndpoint;

    @Value("${minio.external-endpoint:${minio.endpoint}}")
    private String minioExternalEndpoint;

    @Value("${minio.root.user}")
    private String minioAccessKey;

    @Value("${minio.root.password}")
    private String minioSecretKey;

    @Value("${bucket.name}")
    private String bucketName;

    @Primary
    @Bean(name = "minioClient")
    public MinioClient minioClient() {
        MinioClient client = MinioClient.builder()
                .endpoint(minioEndpoint)
                .credentials(minioAccessKey, minioSecretKey)
                .build();

        inicializarBucket(client);
        return client;
    }

    @Bean(name = "minioPresignerClient")
    public MinioClient minioPresignerClient() {
        if (minioExternalEndpoint != null && !minioExternalEndpoint.isBlank() && !minioExternalEndpoint.equals(minioEndpoint)) {
            return MinioClient.builder()
                    .endpoint(minioExternalEndpoint)
                    .credentials(minioAccessKey, minioSecretKey)
                    .build();
        }
        return minioClient();
    }

    public void inicializarBucket(MinioClient client) {
        if (bucketName == null || bucketName.isBlank()) {
            log.warn("Variável 'bucket.name' (BUCKET_NAME) não está definida; provisionamento do bucket ignorado.");
            return;
        }

        try {
            boolean exists = client.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );

            if (!exists) {
                client.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build()
                );
                log.info("Bucket '{}' criado automaticamente no MinIO ({}).", bucketName, minioEndpoint);
            } else {
                log.info("Bucket '{}' já existe no MinIO ({}).", bucketName, minioEndpoint);
            }
        } catch (ErrorResponseException e) {
            String code = e.errorResponse() != null ? e.errorResponse().code() : "UNKNOWN";
            if ("InvalidAccessKeyId".equals(code) || "SignatureDoesNotMatch".equals(code) || "AccessDenied".equals(code)) {
                log.error("Falha de autenticação ou permissão no MinIO ({}) ao verificar/criar o bucket '{}' (código: {}). Verifique as credenciais configuradas.",
                        minioEndpoint, bucketName, code, e);
            } else {
                log.error("Erro retornado pelo MinIO ({}) ao verificar/criar o bucket '{}' (código: {}): {}",
                        minioEndpoint, bucketName, code, e.getMessage(), e);
            }
        } catch (Exception e) {
            if (e instanceof ConnectException || e.getCause() instanceof ConnectException) {
                log.warn("MinIO indisponível em '{}' durante a inicialização; não foi possível verificar/criar o bucket '{}'.",
                        minioEndpoint, bucketName);
            } else {
                log.error("Não foi possível verificar ou criar o bucket '{}' no MinIO ({}): {}",
                        bucketName, minioEndpoint, e.getMessage(), e);
            }
        }
    }

}
