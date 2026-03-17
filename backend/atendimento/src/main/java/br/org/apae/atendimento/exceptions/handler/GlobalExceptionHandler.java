package br.org.apae.atendimento.exceptions.handler;

import br.org.apae.atendimento.exceptions.ErrorResponse;
import br.org.apae.atendimento.exceptions.invalid.*;
import br.org.apae.atendimento.exceptions.notfound.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import br.org.apae.atendimento.exceptions.CloudStorageException;

import java.time.LocalDateTime;

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

    @ExceptionHandler(AgendamentoInvalidException.class)
    public ResponseEntity<ErrorResponse> handleAgendamentoInvalid(AgendamentoInvalidException ex){
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(AtendimentoInvalidException.class)
    public ResponseEntity<ErrorResponse> handleRelacaoInvalid(AtendimentoInvalidException ex){
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ConsultaInvalidException.class)
    public ResponseEntity<ErrorResponse> handleConsultaInvalid(ConsultaInvalidException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(RelacaoInvalidException.class)
    public ResponseEntity<ErrorResponse> handleRelacaoInvalid(RelacaoInvalidException ex){
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(TopicoInvalidException.class)
    public ResponseEntity<ErrorResponse> handleTopicInvalid(TopicoInvalidException ex){
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String mensagem = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return buildResponse(HttpStatus.BAD_REQUEST, mensagem);
    }

    @ExceptionHandler(CloudStorageException.class)
    public ResponseEntity<ErrorResponse> handleCloudStorage(CloudStorageException ex){
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, "Erro ao processar arquivos: " + ex.getMessage());
    }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        return buildResponse(HttpStatus.PAYLOAD_TOO_LARGE, "O arquivo enviado é muito grande. Tente um menor.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ex.printStackTrace();
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro interno inesperado no servidor.");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(HttpMessageNotReadableException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Os dados enviados possuem um formato inválido.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        return buildResponse(HttpStatus.CONFLICT, "Operação não permitida: O registo já existe ou está a ser utilizado por outro processo.");
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