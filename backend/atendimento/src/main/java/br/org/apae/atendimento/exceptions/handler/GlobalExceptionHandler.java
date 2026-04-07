package br.org.apae.atendimento.exceptions.handler;

import br.org.apae.atendimento.exceptions.ErrorResponse;
import br.org.apae.atendimento.exceptions.invalid.*;
import br.org.apae.atendimento.exceptions.notfound.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import br.org.apae.atendimento.exceptions.CloudStorageException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message) {
        ErrorResponse error = new ErrorResponse(LocalDateTime.now(), status.value(), message);
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler({
            PacienteNotFoundException.class,
            ProfissionalSaudeNotFoundException.class,
            AgendamentoNotFoundException.class,
            AtendimentoNotFoundException.class,
            ConsultaNotFoundException.class,
            TipoArquivoNotFoundException.class,
            ArquivoNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({
            AgendamentoInvalidException.class,
            AtendimentoInvalidException.class,
            ConsultaInvalidException.class,
            RelacaoInvalidException.class,
            TopicoInvalidException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequestExceptions(RuntimeException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String mensagensDeErro = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(" | "));

        return buildResponse(HttpStatus.BAD_REQUEST, "Verifique os campos: " + mensagensDeErro);
    }

    @ExceptionHandler(CloudStorageException.class)
    public ResponseEntity<ErrorResponse> handleCloudStorage(CloudStorageException ex){
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, "Erro ao processar arquivos: " + ex.getMessage());
    }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        return buildResponse(HttpStatus.PAYLOAD_TOO_LARGE, "O arquivo enviado é muito grande. Tente um menor.");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Recurso não encontrado.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Erro interno não tratado pelo sistema: ", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro interno inesperado no servidor.");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(HttpMessageNotReadableException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Os dados enviados possuem um formato inválido ou estão mal estruturados.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        String mensagemExata = ex.getMostSpecificCause().getMessage();

        if (mensagemExata != null) {
            String mensagemMinuscula = mensagemExata.toLowerCase();

            if (mensagemMinuscula.contains("duplicate") || mensagemMinuscula.contains("unique")) {
                return buildResponse(HttpStatus.CONFLICT, "Operação não permitida: Este registro já está cadastrado no sistema.");
            }

            if (mensagemMinuscula.contains("foreign key") || mensagemMinuscula.contains("constraint")) {
                return buildResponse(HttpStatus.CONFLICT, "Operação não permitida: Este registro está vinculado a outros dados e não pode ser excluído ou alterado.");
            }
        }

        return buildResponse(HttpStatus.CONFLICT, "Operação não permitida por violação de integridade no banco de dados.");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String mensagem = String.format("O parâmetro '%s' recebeu um valor inválido: '%s'.", ex.getName(), ex.getValue());
        return buildResponse(HttpStatus.BAD_REQUEST, mensagem);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, "Método de requisição não suportado para esta rota.");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "Acesso negado: Você não tem permissão para realizar esta ação.");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex) {
        String mensagem = String.format("O parâmetro obrigatório '%s' não foi enviado na requisição.", ex.getParameterName());
        return buildResponse(HttpStatus.BAD_REQUEST, mensagem);
    }
}