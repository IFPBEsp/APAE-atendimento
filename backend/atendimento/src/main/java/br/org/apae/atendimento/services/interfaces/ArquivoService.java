package br.org.apae.atendimento.services.interfaces;

import br.org.apae.atendimento.dtos.ArquivoDTO;
import br.org.apae.atendimento.entities.Anexo;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ArquivoService<T> {
    T salvar(MultipartFile file, ArquivoDTO metadata);
    String criarObjectName(Long profissionalId);
    List<T> listarPorProfissionalEPaciente(Long profissional, UUID pacienteId);
    List<T> buscarPorData(Long profissionalId, UUID pacienteId, LocalDate data);
    void deletar(String bucket, String objectName);
}
