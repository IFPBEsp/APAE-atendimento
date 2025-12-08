package br.org.apae.atendimento.exceptions;

public class AgendamentoNotFoundException extends RuntimeException{
    public AgendamentoNotFoundException() {
        super("Agendamento não encontrado");
    }
}
