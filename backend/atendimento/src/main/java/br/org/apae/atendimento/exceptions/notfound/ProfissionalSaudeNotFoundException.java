package br.org.apae.atendimento.exceptions.notfound;

public class ProfissionalSaudeNotFoundException extends RuntimeException{
    public ProfissionalSaudeNotFoundException(){
        super("O profissional de saúde não foi encontrado.");
    }
}
