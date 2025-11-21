package br.org.apae.atendimento.exceptions.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.org.apae.atendimento.exceptions.*;

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
}