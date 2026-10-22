package br.org.apae.atendimento.services.storage;

public interface PresignedUrlService {
    String gerarUrlPreAssinada(String objectName);
    default String gerarUrlPreAssinada(String objectName, String nomeArquivo) {
        return gerarUrlPreAssinada(objectName);
    }
    void evictUrlFromCache(String objectName);
}
