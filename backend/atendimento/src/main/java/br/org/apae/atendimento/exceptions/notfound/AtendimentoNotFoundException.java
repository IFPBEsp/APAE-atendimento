package br.org.apae.atendimento.exceptions.notfound;

public class AtendimentoNotFoundException extends RuntimeException{
    public AtendimentoNotFoundException() {
        super("Atendimento não encontrado");
    }
}
