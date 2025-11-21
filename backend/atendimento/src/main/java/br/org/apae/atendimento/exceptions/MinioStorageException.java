package br.org.apae.atendimento.exceptions;

public class MinioStorageException extends RuntimeException{
    public MinioStorageException(String message) {
        super(message);
    }

    public MinioStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
