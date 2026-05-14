package br.org.apae.atendimento.repositories;

import br.org.apae.atendimento.entities.Atendimento;
import br.org.apae.atendimento.entities.views.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AtendimentoRepository extends JpaRepository<Atendimento, UUID> {
    List<Atendimento> findByPacienteIdAndProfissionalIdOrderByDataAtendimento(UUID pacienteId, UUID profissionalId);

    @Query("SELECT MAX(a.numeracao) " +
            "FROM Atendimento a " +
            "WHERE MONTH(a.dataAtendimento) = :mes " +
            "AND YEAR(a.dataAtendimento) = :ano " +
            "AND a.profissionalId = :profissionalId")
    Long findMaxNumeracaoByMesAndAno(@Param("mes") int mes, @Param("ano") int ano, @Param("profissionalId") UUID profissionalId);

    @Query("""
        SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
        FROM Atendimento a
        WHERE a.dataAtendimento >= :inicioDia
          AND a.dataAtendimento < :fimDia
          AND a.profissionalId = :profissionalId
          AND a.pacienteId = :pacienteId
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

    @Query(value = """
        SELECT DISTINCT p.*
        FROM atendimento.vw_pacientes p
        INNER JOIN atendimento.atendimento a ON a.paciente_id = p.paciente_id
        WHERE a.profissional_id = :profissionalId
        """, nativeQuery = true)
    List<Paciente> findPacientesByProfissionalId(@Param("profissionalId") UUID profissionalId);
}
