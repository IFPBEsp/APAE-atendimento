package br.org.apae.atendimento.exceptions.invalid;

public class AgendamentoInvalidException extends RuntimeException{
    public AgendamentoInvalidException(String message) {
        super(message);
    }
}
