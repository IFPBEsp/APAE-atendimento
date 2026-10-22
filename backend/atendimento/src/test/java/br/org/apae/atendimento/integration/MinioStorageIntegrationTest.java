package br.org.apae.atendimento.integration;

import br.org.apae.atendimento.config.MinioConfig;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Volume;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class MinioStorageIntegrationTest {

    private static final String ACCESS_KEY = "atendimento_minio";
    private static final String SECRET_KEY = "atendimento_minio_123";
    private static final String TEST_BUCKET = "apae-atendimento-integration-test";

    @Test
    @DisplayName("Deve provisionar bucket ausente e preservar arquivo apos reiniciar armazenamento MinIO real")
    void deveProvisionarBucketAusenteEPreservarArquivoAposReiniciarArmazenamento() throws Exception {
        String volumeName = "minio-test-volume-" + UUID.randomUUID();
        DockerClient dockerClient = DockerClientFactory.instance().client();
        dockerClient.createVolumeCmd().withName(volumeName).exec();

        byte[] conteudoArquivo = "Conteudo de relatorio para teste de integracao".getBytes(StandardCharsets.UTF_8);
        String objectName = "relatorios/2026/teste-preservacao.pdf";

        try {
            // ==========================================
            // FASE 1: Instância inicial do MinIO
            // ==========================================
            try (GenericContainer<?> minio1 = new GenericContainer<>("minio/minio:latest")
                    .withEnv("MINIO_ROOT_USER", ACCESS_KEY)
                    .withEnv("MINIO_ROOT_PASSWORD", SECRET_KEY)
                    .withCommand("server /data")
                    .withCreateContainerCmdModifier(cmd -> cmd.getHostConfig().withBinds(new Bind(volumeName, new Volume("/data"))))
                    .withExposedPorts(9000)
                    .waitingFor(Wait.forHttp("/minio/health/ready").forPort(9000).withStartupTimeout(Duration.ofSeconds(60)))) {

                minio1.start();

                String endpoint1 = "http://" + minio1.getHost() + ":" + minio1.getMappedPort(9000);
                MinioClient client1 = MinioClient.builder()
                        .endpoint(endpoint1)
                        .credentials(ACCESS_KEY, SECRET_KEY)
                        .build();

                MinioConfig config1 = new MinioConfig();
                ReflectionTestUtils.setField(config1, "bucketName", TEST_BUCKET);
                ReflectionTestUtils.setField(config1, "minioEndpoint", endpoint1);
                ReflectionTestUtils.setField(config1, "minioAccessKey", ACCESS_KEY);
                ReflectionTestUtils.setField(config1, "minioSecretKey", SECRET_KEY);

                // 1. Comprova que o bucket não existe inicialmente
                boolean existeInicialmente = client1.bucketExists(
                        BucketExistsArgs.builder().bucket(TEST_BUCKET).build()
                );
                assertFalse(existeInicialmente, "O bucket nao deve existir antes do provisionamento");

                // 2. Executa o provisionamento automático
                config1.inicializarBucket(client1);

                // 3. Comprova que o bucket foi criado com sucesso
                boolean existeAposProvisionamento = client1.bucketExists(
                        BucketExistsArgs.builder().bucket(TEST_BUCKET).build()
                );
                assertTrue(existeAposProvisionamento, "O bucket deve existir apos o provisionamento");

                // 4. Faz upload de um arquivo de teste
                client1.putObject(
                        PutObjectArgs.builder()
                                .bucket(TEST_BUCKET)
                                .object(objectName)
                                .stream(new ByteArrayInputStream(conteudoArquivo), conteudoArquivo.length, -1)
                                .contentType("application/pdf")
                                .build()
                );

                // Valida que o arquivo foi gravado
                try (InputStream stream = client1.getObject(
                        GetObjectArgs.builder().bucket(TEST_BUCKET).object(objectName).build()
                )) {
                    assertArrayEquals(conteudoArquivo, stream.readAllBytes());
                }

                // Finaliza o container 1 (simula o encerramento do armazenamento)
                minio1.stop();
            }

            // ==========================================
            // FASE 2: Reinicialização do MinIO (mesmo volume)
            // ==========================================
            try (GenericContainer<?> minio2 = new GenericContainer<>("minio/minio:latest")
                    .withEnv("MINIO_ROOT_USER", ACCESS_KEY)
                    .withEnv("MINIO_ROOT_PASSWORD", SECRET_KEY)
                    .withCommand("server /data")
                    .withCreateContainerCmdModifier(cmd -> cmd.getHostConfig().withBinds(new Bind(volumeName, new Volume("/data"))))
                    .withExposedPorts(9000)
                    .waitingFor(Wait.forHttp("/minio/health/ready").forPort(9000).withStartupTimeout(Duration.ofSeconds(60)))) {

                minio2.start();

                String endpoint2 = "http://" + minio2.getHost() + ":" + minio2.getMappedPort(9000);
                MinioClient client2 = MinioClient.builder()
                        .endpoint(endpoint2)
                        .credentials(ACCESS_KEY, SECRET_KEY)
                        .build();

                MinioConfig config2 = new MinioConfig();
                ReflectionTestUtils.setField(config2, "bucketName", TEST_BUCKET);
                ReflectionTestUtils.setField(config2, "minioEndpoint", endpoint2);
                ReflectionTestUtils.setField(config2, "minioAccessKey", ACCESS_KEY);
                ReflectionTestUtils.setField(config2, "minioSecretKey", SECRET_KEY);

                // 5. Executa novamente o provisionamento (idempotência)
                config2.inicializarBucket(client2);

                // 6. Comprova que o bucket continua existindo
                boolean existeAposReinicio = client2.bucketExists(
                        BucketExistsArgs.builder().bucket(TEST_BUCKET).build()
                );
                assertTrue(existeAposReinicio, "O bucket deve continuar existindo apos o reinicio");

                // 7. Comprova que o arquivo foi preservado intacto
                try (InputStream stream = client2.getObject(
                        GetObjectArgs.builder().bucket(TEST_BUCKET).object(objectName).build()
                )) {
                    byte[] bytesLidos = stream.readAllBytes();
                    assertArrayEquals(conteudoArquivo, bytesLidos, "O conteudo do arquivo deve ser preservado intacto apos o reinicio do MinIO");
                }
            }
        } finally {
            try {
                dockerClient.removeVolumeCmd(volumeName).exec();
            } catch (Exception ignored) {
            }
        }
    }
}
