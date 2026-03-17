package br.org.apae.atendimento.exceptions;

public class CloudStorageException extends RuntimeException{
    public CloudStorageException(String message) {
        super(message);
    }

    public CloudStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
