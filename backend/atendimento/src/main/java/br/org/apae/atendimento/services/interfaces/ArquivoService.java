package br.org.apae.atendimento.services.interfaces;

import br.org.apae.atendimento.entities.Anexo;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ArquivoService<T> {
    T salvar(UUID pacienteId, Long profissionalId, MultipartFile file, LocalDate date);
    String criarObjectName(Long profissionalId);
    List<T> listarPorProfissionalEPaciente(Long profissional, UUID pacienteId);
    List<T> buscarPorData(Long profissionalId, UUID pacienteId, LocalDate data);

}
