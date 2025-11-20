package br.org.apae.atendimento.entities.interfaces;

public interface ArquivoStorage {
    String getObjectName();
    String getBucket();
    String getPresignedUrl();
    void setPresignedUrl(String presignedUrl);
}
