package br.org.apae.atendimento.services.storage;

import br.org.apae.atendimento.config.MinioConfig;
import br.org.apae.atendimento.controllers.ArquivoController;
import br.org.apae.atendimento.dtos.request.ArquivoRequestDTO;
import br.org.apae.atendimento.exceptions.CloudStorageException;
import br.org.apae.atendimento.exceptions.handler.GlobalExceptionHandler;
import br.org.apae.atendimento.security.UsuarioAutenticado;
import br.org.apae.atendimento.services.ArquivoService;
import br.org.apae.atendimento.services.storage.s3.S3PresignedUrlService;
import br.org.apae.atendimento.services.storage.s3.S3Service;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;

import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorageProvisioningTest {

    @Mock
    private MinioClient minioClient;

    @Mock
    private S3Client s3Client;

    @Mock
    private S3PresignedUrlService s3PresignedUrlService;

    @Mock
    private ArquivoService arquivoService;

    private MinioConfig minioConfig;
    private S3Service s3Service;

    @BeforeEach
    void setUp() {
        minioConfig = new MinioConfig();
        ReflectionTestUtils.setField(minioConfig, "minioEndpoint", "http://localhost:9100");
        ReflectionTestUtils.setField(minioConfig, "minioAccessKey", "atendimento_minio");
        ReflectionTestUtils.setField(minioConfig, "minioSecretKey", "atendimento_minio_123");
        ReflectionTestUtils.setField(minioConfig, "bucketName", "apae-atendimento");

        s3Service = new S3Service(s3Client, s3PresignedUrlService);
        ReflectionTestUtils.setField(s3Service, "BUCKET_NAME", "apae-atendimento");
        ReflectionTestUtils.setField(s3Service, "autoProvision", false);
    }

    @Test
    @DisplayName("MinioConfig deve criar o bucket automaticamente quando ausente")
    void minioConfigDeveCriarBucketQuandoAusente() throws Exception {
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(false);

        assertDoesNotThrow(() -> minioConfig.inicializarBucket(minioClient));

        ArgumentCaptor<MakeBucketArgs> captor = ArgumentCaptor.forClass(MakeBucketArgs.class);
        verify(minioClient, times(1)).makeBucket(captor.capture());
        assertEquals("apae-atendimento", captor.getValue().bucket());
    }

    @Test
    @DisplayName("MinioConfig deve preservar o bucket quando ja existente na reinicializacao")
    void minioConfigDevePreservarBucketQuandoExistente() throws Exception {
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);

        assertDoesNotThrow(() -> minioConfig.inicializarBucket(minioClient));

        verify(minioClient, times(1)).bucketExists(any(BucketExistsArgs.class));
        verify(minioClient, never()).makeBucket(any(MakeBucketArgs.class));
    }

    @Test
    @DisplayName("MinioConfig nao deve quebrar a inicializacao quando MinIO estiver indisponivel")
    void minioConfigNaoDeveQuebrarQuandoMinioIndisponivel() throws Exception {
        when(minioClient.bucketExists(any(BucketExistsArgs.class)))
                .thenThrow(new ConnectException("Connection refused"));

        assertDoesNotThrow(() -> minioConfig.inicializarBucket(minioClient));
        verify(minioClient, never()).makeBucket(any(MakeBucketArgs.class));
    }

    @Test
    @DisplayName("S3Service deve criar o bucket quando ausente e autoProvision estiver ativado")
    void s3ServiceDeveCriarBucketQuandoAusenteEAutoProvisionAtivado() {
        ReflectionTestUtils.setField(s3Service, "autoProvision", true);
        when(s3Client.headBucket(any(HeadBucketRequest.class)))
                .thenThrow(NoSuchBucketException.builder().message("The specified bucket does not exist").build());

        assertDoesNotThrow(() -> s3Service.inicializarBucket());

        ArgumentCaptor<CreateBucketRequest> captor = ArgumentCaptor.forClass(CreateBucketRequest.class);
        verify(s3Client, times(1)).createBucket(captor.capture());
        assertEquals("apae-atendimento", captor.getValue().bucket());
    }

    @Test
    @DisplayName("S3Service nao deve criar o bucket quando ausente se autoProvision estiver desativado (Terraform/Infra)")
    void s3ServiceNaoDeveCriarBucketQuandoAutoProvisionDesativado() {
        ReflectionTestUtils.setField(s3Service, "autoProvision", false);
        when(s3Client.headBucket(any(HeadBucketRequest.class)))
                .thenThrow(NoSuchBucketException.builder().message("The specified bucket does not exist").build());

        assertDoesNotThrow(() -> s3Service.inicializarBucket());

        verify(s3Client, never()).createBucket(any(CreateBucketRequest.class));
    }

    @Test
    @DisplayName("S3Service deve preservar o bucket existente na reinicializacao")
    void s3ServiceDevePreservarBucketQuandoExistente() {
        when(s3Client.headBucket(any(HeadBucketRequest.class)))
                .thenReturn(HeadBucketResponse.builder().build());

        assertDoesNotThrow(() -> s3Service.inicializarBucket());

        verify(s3Client, times(1)).headBucket(any(HeadBucketRequest.class));
        verify(s3Client, never()).createBucket(any(CreateBucketRequest.class));
    }

    @Test
    @DisplayName("S3Service nao deve quebrar a inicializacao no profile prod quando S3 estiver indisponivel")
    void s3ServiceNaoDeveQuebrarInicializacaoQuandoIndisponivel() {
        when(s3Client.headBucket(any(HeadBucketRequest.class)))
                .thenThrow(SdkClientException.create("Unable to execute HTTP request"));

        assertDoesNotThrow(() -> s3Service.inicializarBucket());
    }

    @Test
    @DisplayName("ArquivoController deve propagar CloudStorageException para retornar HTTP 503 via GlobalExceptionHandler")
    void arquivoControllerDevePropagarCloudStorageExceptionPara503() {
        ArquivoController controller = new ArquivoController();
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        ReflectionTestUtils.setField(controller, "service", arquivoService);
        ReflectionTestUtils.setField(controller, "validator", validator);

        UUID pacienteId = UUID.randomUUID();
        UUID profissionalId = UUID.randomUUID();
        UsuarioAutenticado usuario = new UsuarioAutenticado(profissionalId);

        MockMultipartFile file = new MockMultipartFile("file", "laudo.pdf", "application/pdf", "pdf".getBytes(StandardCharsets.UTF_8));
        String metadataJson = """
                {"data":"%s","tipoArquivo":1,"pacienteId":"%s","titulo":"Laudo","descricao":"Descricao"}
                """.formatted(LocalDate.now(), pacienteId);

        when(arquivoService.salvar(any(), any(ArquivoRequestDTO.class), eq(profissionalId)))
                .thenThrow(new CloudStorageException("Erro ao enviar arquivo para a nuvem."));

        CloudStorageException thrown = assertThrows(CloudStorageException.class,
                () -> controller.upload(file, metadataJson, usuario));

        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        ResponseEntity<?> response = handler.handleCloudStorage(thrown);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    }
}
