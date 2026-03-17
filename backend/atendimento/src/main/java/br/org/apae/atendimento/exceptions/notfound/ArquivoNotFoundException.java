package br.org.apae.atendimento.exceptions.notfound;

public class ArquivoNotFoundException extends RuntimeException {

    public ArquivoNotFoundException() {
        super("Arquivo não encontrado no sistema.");
    }

    public ArquivoNotFoundException(String message) {
        super(message);
    }
}
