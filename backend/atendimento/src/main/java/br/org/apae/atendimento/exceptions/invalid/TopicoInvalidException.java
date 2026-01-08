package br.org.apae.atendimento.exceptions.invalid;

public class TopicoInvalidException extends IllegalArgumentException{
    public TopicoInvalidException(String s) {
        super(s);
    }
}
