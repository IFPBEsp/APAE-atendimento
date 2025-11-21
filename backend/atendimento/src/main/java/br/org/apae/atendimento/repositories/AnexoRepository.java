package br.org.apae.atendimento.repositories;

import br.org.apae.atendimento.entities.Anexo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface AnexoRepository extends JpaRepository<Anexo, String> {
    List<Anexo> findByProfissionalIdAndPacienteId(Long profissionalId, UUID pacienteId);
    List<Anexo> findByProfissionalIdAndPacienteIdAndData(
            Long profissionalId,
            UUID pacienteId,
            LocalDate data
    );
}
