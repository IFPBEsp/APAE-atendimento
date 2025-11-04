package br.org.apae.atendimento.exceptions;

public class ConsultaNotFoundException extends RuntimeException{
    ConsultaNotFoundException(){
        super("A consulta que você procura não foi encontrada.");
    }
}