package br.org.apae.atendimento.exceptions;

public class ProfissionalSaudeNotFoundException extends RuntimeException{
    ProfissionalSaudeNotFoundException(){
        super("O profissional de saúde não foi encontrado.");
    }
}
