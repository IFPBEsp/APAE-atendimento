package br.org.apae.atendimento.exceptions.invalid;

public class RelacaoInvalidException extends RuntimeException{
    public RelacaoInvalidException() {
        super("As entidades não se relacionam");
    }

    public RelacaoInvalidException(String message) {
        super(message);
    }
}
