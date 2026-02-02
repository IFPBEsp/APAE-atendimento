package br.org.apae.atendimento.exceptions.invalid;

public class AtendimentoInvalidException extends RuntimeException{
    public AtendimentoInvalidException(String message) {
        super(message);
    }
}
