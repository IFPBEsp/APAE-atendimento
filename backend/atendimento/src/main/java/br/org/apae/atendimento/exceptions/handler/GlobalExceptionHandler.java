package br.org.apae.atendimento.exceptions.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import br.org.apae.atendimento.exceptions.MinioStorageException;
import br.org.apae.atendimento.exceptions.invalid.AgendamentoInvalidException;
import br.org.apae.atendimento.exceptions.invalid.AtendimentoInvalidException;
import br.org.apae.atendimento.exceptions.invalid.ConsultaInvalidException;
import br.org.apae.atendimento.exceptions.invalid.RelacaoInvalidException;
import br.org.apae.atendimento.exceptions.notfound.AgendamentoNotFoundException;
import br.org.apae.atendimento.exceptions.notfound.AtendimentoNotFoundException;
import br.org.apae.atendimento.exceptions.notfound.ConsultaNotFoundException;
import br.org.apae.atendimento.exceptions.notfound.PacienteNotFoundException;
import br.org.apae.atendimento.exceptions.notfound.ProfissionalSaudeNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConsultaNotFoundException.class)
    public ResponseEntity<String> handleConsultaNotFound(ConsultaNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(PacienteNotFoundException.class)
    public ResponseEntity<String> handlePacienteNotFound(PacienteNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(ProfissionalSaudeNotFoundException.class)
    public ResponseEntity<String> handleProfissionalSaudeNotFound(ProfissionalSaudeNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler(ConsultaInvalidException.class)
    public ResponseEntity<String> handleConsultaInvalid(ConsultaInvalidException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(MinioStorageException.class)
    public ResponseEntity<String> handleMinioStorage(MinioStorageException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(RelacaoInvalidException.class)
    public ResponseEntity<String> handleRelacaoInvalid(RelacaoInvalidException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(AgendamentoInvalidException.class)
    public ResponseEntity<String> agendamentoInvalidException(AgendamentoInvalidException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(AgendamentoNotFoundException.class)
    public ResponseEntity<String> handleRelacaoInvalid(AgendamentoNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(AtendimentoInvalidException.class)
    public ResponseEntity<String> handleRelacaoInvalid(AtendimentoInvalidException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(AtendimentoNotFoundException.class)
    public ResponseEntity<String> handleRelacaoInvalid(AtendimentoNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    
  @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body("{\"error\":\"O arquivo enviado é muito grande. Tente um menor.\"}");
    }

    
}