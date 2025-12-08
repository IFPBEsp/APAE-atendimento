package br.org.apae.atendimento.exceptions.notfound;

public class ConsultaNotFoundException extends RuntimeException{
    ConsultaNotFoundException(){
        super("A consulta que você procura não foi encontrada.");
    }
}