package br.org.apae.atendimento.services;

import br.org.apae.atendimento.dtos.ArquivoDTO;
import br.org.apae.atendimento.entities.interfaces.ArquivoStorage;
import br.org.apae.atendimento.exceptions.MinioStorageException;
import io.minio.*;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MinioService {

    private final MinioClient client;

    @Autowired
    public MinioService(MinioClient client) {
        this.client = client;
    }

    public void uploadArquivo(String bucket, String objectName, MultipartFile file) {

        if (objectName == null || objectName.isBlank()) {
            throw new MinioStorageException("O nome do arquivo no MinIO não pode ser vazio.");
        }

        try {
            criarBucketSeNaoExiste(bucket);
            colocarArquivo(bucket, objectName, file);

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



    private void colocarArquivo(String bucket, String objectName, MultipartFile file) {
        try (InputStream is = file.getInputStream()) {

            String contentType = file.getContentType();
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            client.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
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


    public List<? extends ArquivoStorage> capturarArquivos(List<? extends ArquivoStorage> arquivos) {
        try {
            return arquivos.stream()
                    .map(this::processarArquivo)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new MinioStorageException("Erro ao capturar arquivos", e);
        }
    }

    private <T extends ArquivoStorage> T processarArquivo(T arquivo) {
        try {
            String url = gerarUrlPreAssinada(arquivo.getBucket(), arquivo.getId());
            arquivo.setUrl(url);
            return arquivo;
        } catch (Exception e) {
            throw new MinioStorageException("Erro ao processar arquivo", e);
        }
    }

    public String gerarUrlPreAssinada(String bucket, String objectName) {
        try {
            String url = client.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .method(io.minio.http.Method.GET)
                            .expiry(60 * 60)
                            .build()
            );
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            throw new MinioStorageException("Erro ao gerar URL para " + bucket + "/" + objectName + ": " + e.getMessage(), e);
        }
    }
}
