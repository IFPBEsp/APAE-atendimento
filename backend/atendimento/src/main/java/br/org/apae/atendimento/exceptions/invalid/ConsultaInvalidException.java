package br.org.apae.atendimento.exceptions.invalid;

public class ConsultaInvalidException extends IllegalArgumentException{
    public ConsultaInvalidException(String motivoInvalidez){
        super(String.format("Consulta precisa ter um %s", motivoInvalidez));
    }
}
