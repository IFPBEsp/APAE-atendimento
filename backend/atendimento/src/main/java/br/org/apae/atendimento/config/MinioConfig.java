package br.org.apae.atendimento.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TesteMinioConfig {

    @Value("${minio.endpoint:http://localhost:9000}")
    private String minioEndpoint;

    @Value("${minio.root.user}")
    private String minioAccessKey;

    @Value("${minio.root.password}")
    private String minioSecretKey;

    // Se você for definir um bucket padrão no seu MinIO e quiser injetar aqui
    // @Value("${minio.bucketName:default-bucket}")
    // private String minioBucketName;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioEndpoint)
                .credentials(minioAccessKey, minioSecretKey)
                .build();
    }
    
}