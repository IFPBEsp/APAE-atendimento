package br.org.apae.atendimento.exceptions;

public class PacienteNotFoundException extends RuntimeException{
    PacienteNotFoundException(){
        super("Paciente não encontrado.");
    }
}
