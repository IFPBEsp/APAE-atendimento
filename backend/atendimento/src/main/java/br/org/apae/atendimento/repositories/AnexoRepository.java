package br.org.apae.atendimento.repositories;

import br.org.apae.atendimento.entities.Arquivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface AnexoRepository extends JpaRepository<Arquivo, String> {
    List<Arquivo> findByProfissionalIdAndPacienteIdAndTipoId(UUID profissionalId, UUID pacienteId, Long tipoId);
    List<Arquivo> findByProfissionalIdAndPacienteIdAndDataAndTipoId(
            UUID profissionalId,
            UUID pacienteId,
            LocalDate data,
            Long tipoId
    );
}
