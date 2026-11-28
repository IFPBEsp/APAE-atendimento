package br.org.apae.atendimento.repositories;

import br.org.apae.atendimento.entities.Agendamento;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AgendamentoRepository extends JpaRepository<Agendamento, UUID> {
    List<Agendamento> findByProfissionalIdOrderByDataHoraDesc(UUID profissionalId);

    List<Agendamento> findByProfissionalIdAndDataHoraBetweenOrderByDataHoraDesc(
            UUID profissionalId, LocalDateTime dataInicio, LocalDateTime dataFim);

    boolean existsByProfissionalIdAndDataHora(UUID profissionalId, LocalDateTime dataHora);

    Optional<Agendamento> findByIdAndProfissionalIdAndPacienteId(
            UUID id,
            UUID profissionalId,
            UUID pacienteId
    );

    Optional<Agendamento> findByIdAndProfissionalId(
            UUID id,
            UUID profissionalId
    );
}