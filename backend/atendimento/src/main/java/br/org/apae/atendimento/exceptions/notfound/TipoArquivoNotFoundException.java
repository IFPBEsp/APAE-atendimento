package br.org.apae.atendimento.exceptions.notfound;

public class TipoArquivoNotFoundException extends RuntimeException {

    public TipoArquivoNotFoundException() {
        super("Tipo de arquivo não encontrado.");
    }

    public TipoArquivoNotFoundException(String message) {
        super(message);
    }
}
