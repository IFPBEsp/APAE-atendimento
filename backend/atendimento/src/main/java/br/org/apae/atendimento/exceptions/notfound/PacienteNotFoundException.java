package br.org.apae.atendimento.exceptions.notfound;

public class PacienteNotFoundException extends RuntimeException{
    public PacienteNotFoundException(){
        super("Paciente não encontrado.");
    }

    public PacienteNotFoundException(String message) {
        super(message);
    }
}
