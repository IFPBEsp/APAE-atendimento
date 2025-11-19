package br.org.apae.atendimento.entities.interfaces;

public interface ArquivoStorage {
    String getId();
    String getBucket();
    String getUrl();
    void setUrl(String url);
}
