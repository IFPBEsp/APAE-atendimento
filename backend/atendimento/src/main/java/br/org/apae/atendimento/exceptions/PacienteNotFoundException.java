package br.org.apae.atendimento.exceptions;

public class PacienteNotFoundException extends RuntimeException{
    public PacienteNotFoundException(){
        super("Paciente não encontrado.");
    }
}
