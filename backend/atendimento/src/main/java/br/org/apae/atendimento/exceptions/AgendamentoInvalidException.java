package br.org.apae.atendimento.exceptions;

public class AgendamentoInvalidException extends RuntimeException{
    public AgendamentoInvalidException(String message) {
        super(message);
    }
}
