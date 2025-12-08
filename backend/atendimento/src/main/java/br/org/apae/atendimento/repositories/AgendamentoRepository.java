package br.org.apae.atendimento.repositories;

import br.org.apae.atendimento.entities.Agendamento;

import br.org.apae.atendimento.entities.Atendimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AgendamentoRepository extends JpaRepository<Agendamento, UUID> {
    List<Agendamento> findByProfissionalIdOrderByDataHoraDesc(UUID profissionalId);
    @Query("""
       SELECT a 
       FROM Agendamento a
       WHERE a.dataHora >= :dataInicio
         AND a.dataHora <  :dataFim
         AND a.paciente.id = :pacienteId
    """)
    Optional<Agendamento> findByDataHoraAndPacienteId(
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            @Param("pacienteId") UUID pacienteId
    );

    boolean existsByProfissionalIdAndPacienteIdAndDataHora(UUID profissionalId, UUID pacienteId, LocalDateTime dataHora);
}
