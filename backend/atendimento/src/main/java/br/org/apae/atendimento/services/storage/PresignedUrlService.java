package br.org.apae.atendimento.services.storage;

public interface PresignedUrlService {
    String gerarUrlPreAssinada(String objectName);
    void evictUrlFromCache(String objectName);
}
