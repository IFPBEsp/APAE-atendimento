package br.org.apae.atendimento.repositories;

import br.org.apae.atendimento.entities.Atendimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AtendimentoRepository extends JpaRepository<Atendimento, UUID> {
    List<Atendimento> findByPacienteIdAndProfissionalIdOrderByDataAtendimento(UUID pacienteId, UUID profissionalId);
    @Query("""
        SELECT COALESCE(MAX(a.numeracao), 0)
        FROM Atendimento a
        WHERE EXTRACT(MONTH FROM a.dataAtendimento) = :mes
          AND EXTRACT(YEAR FROM a.dataAtendimento) = :ano
          AND a.profissional.id = :profissionalId
          AND a.paciente.id = :pacienteId
    """)
    Long findMaxNumeracaoByMesAndAno(
            int mes,
            int ano,
            UUID profissionalId,
            UUID pacienteId
    );
    @Query("""
        SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
        FROM Atendimento a
        WHERE a.dataAtendimento >= :inicioDia
          AND a.dataAtendimento < :fimDia
          AND a.profissional.id = :profissionalId
          AND a.paciente.id = :pacienteId
    """)
    boolean existsAtendimentoNoDia(
            LocalDateTime inicioDia,
            LocalDateTime fimDia,
            UUID profissionalId,
            UUID pacienteId
    );

    boolean existsByProfissionalIdAndDataAtendimento(
            UUID profissionalId,
            LocalDateTime dataAtendimento
    );
}
