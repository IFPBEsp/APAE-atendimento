package br.org.apae.atendimento.services.storage;

import org.springframework.web.multipart.MultipartFile;

public interface ObjectStorageService {
    String uploadArquivo(String objectName, MultipartFile file);
    void deletarArquivo(String objectName);
}
