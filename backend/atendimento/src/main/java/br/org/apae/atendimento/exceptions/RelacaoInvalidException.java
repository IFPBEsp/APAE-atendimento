package br.org.apae.atendimento.exceptions;

public class RelacaoInvalidException extends RuntimeException{
    public RelacaoInvalidException() {
        super("As entidades não se relacionam");
    }
}
