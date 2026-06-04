package br.org.apae.atendimento.repositories;

import br.org.apae.atendimento.entities.Agendamento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AgendamentoRepository extends JpaRepository<Agendamento, UUID> {
    List<Agendamento> findByProfissionalIdOrderByDataHora(UUID profissionalId);

    @Query("""
       SELECT a
       FROM Agendamento a
       WHERE a.dataHora >= :dataInicio
         AND a.dataHora < :dataFim
         AND a.profissionalId = :profissionalId
         AND a.pacienteId = :pacienteId
    """)
    Optional<Agendamento> findByDataHoraAndProfissionalIdAndPacienteId(
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            @Param("profissionalId") UUID profissionalId,
            @Param("pacienteId") UUID pacienteId
    );

    boolean existsByProfissionalIdAndDataHora(UUID profissionalId, LocalDateTime dataHora);

    Optional<Agendamento> findByIdAndProfissionalIdAndPacienteId(
            UUID id,
            UUID profissionalId,
            UUID pacienteId
    );
}
