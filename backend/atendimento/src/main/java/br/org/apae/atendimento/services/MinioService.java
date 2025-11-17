package br.org.apae.atendimento.services;

import br.org.apae.atendimento.exceptions.MinioStorageException;
import io.minio.*;
import io.minio.errors.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.IOException;

@Service
public class MinioService {

    private final MinioClient client;

    public MinioService(MinioClient client) {
        this.client = client;
    }

    public void uploadArquivo(String bucket, MultipartFile file, String path, String objectName) {

        if (path == null || path.isBlank()) {
            throw new MinioStorageException("O nome do arquivo no MinIO não pode ser vazio.");
        }

        try {
            criarBucketSeNaoExiste(bucket);
            colocarArquivo(bucket, path, objectName, file);

        } catch (MinioStorageException e) {
            throw e;

        } catch (Exception e) {
            throw new MinioStorageException("Erro inesperado ao fazer upload no MinIO.", e);
        }
    }

    private void criarBucketSeNaoExiste(String bucket) {
        try {
            boolean exists = client.bucketExists(
                    BucketExistsArgs.builder().bucket(bucket).build()
            );
            if (!exists) {
                try {
                    client.makeBucket(
                            MakeBucketArgs.builder().bucket(bucket).build()
                    );
                } catch (ErrorResponseException e) {
                    if (!"BucketAlreadyOwnedByYou".equals(e.errorResponse().code())) {
                        throw e;
                    }
                }
            }

        } catch (Exception e) {
            throw new MinioStorageException("Erro ao verificar/criar o bucket '" + bucket + "'.", e);
        }
    }



    private void colocarArquivo(String bucket, String path, String objectName, MultipartFile file) {
        try (InputStream is = file.getInputStream()) {

            String contentType = file.getContentType();
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            client.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(path + "/" + objectName)
                            .stream(is, file.getSize(), -1)
                            .contentType(contentType)
                            .build()
            );

        } catch (ErrorResponseException e) {
            throw new MinioStorageException(
                    "O MinIO retornou erro ao fazer upload do arquivo: " +
                            e.errorResponse().message(), e);

        } catch (IOException e) {
            throw new MinioStorageException(
                    "Falha ao ler o arquivo enviado pelo cliente.", e);

        } catch (Exception e) {
            throw new MinioStorageException(
                    "Erro ao enviar o arquivo para o bucket '" + bucket + "'.", e);
        }
    }

    public ResponseEntity<InputStreamResource> exibir(String bucket, String path, String objectName) {

        String finalPath = path + "/" + objectName;

        try {
            StatObjectResponse stat = client.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(finalPath)
                            .build()
            );

            InputStream is = client.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(finalPath)
                            .build()
            );

            InputStreamResource resource = new InputStreamResource(is);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(stat.contentType()))
                    .contentLength(stat.size())
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + path + "\"")
                    .body(resource);

        } catch (Exception e) {
            throw new MinioStorageException("Erro ao obter o arquivo: " + path, e);
        }
    }
}
