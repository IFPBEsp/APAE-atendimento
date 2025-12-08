package br.org.apae.atendimento.exceptions;

public class AtendimentoInvalidException extends RuntimeException{
    public AtendimentoInvalidException(String message) {
        super(message);
    }
}
