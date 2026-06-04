package br.org.apae.atendimento.repositories;

import br.org.apae.atendimento.entities.Atendimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AtendimentoRepository extends JpaRepository<Atendimento, UUID> {

    @Query(value = """
        SELECT MAX(CAST(numeracao AS BIGINT))
        FROM atendimento.atendimento
        WHERE EXTRACT(MONTH FROM data_atendimento) = :mes
          AND EXTRACT(YEAR FROM data_atendimento) = :ano
          AND profissional_id = :profissionalId
          AND numeracao ~ '^[0-9]+$'
    """, nativeQuery = true)
    Long findMaxNumeracaoByMesAndAno(@Param("mes") int mes, @Param("ano") int ano, @Param("profissionalId") UUID profissionalId);

    @Query("""
        SELECT DISTINCT a
        FROM Atendimento a
        LEFT JOIN FETCH a.relatorio
        WHERE a.pacienteId = :pacienteid
            AND a.profissionalId = :profissionalid
        ORDER BY a.dataAtendimento
    """)
    List<Atendimento> findByPacienteIdAndProfissionalIdComRelatorio(
            @Param("pacienteid") UUID pacienteId,
            @Param("profissionalid") UUID profissionalId
    );

    @Query("""
        SELECT a
        FROM Atendimento a
        LEFT JOIN FETCH a.relatorio
        WHERE a.id = :atendimentoId
    """)
    Optional<Atendimento> findByIdComRelatorio(@Param("atendimentoId") UUID atendimentoId);

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

    Optional<Atendimento> findByIdAndProfissionalIdAndPacienteId(
            UUID id,
            UUID profissionalId,
            UUID pacienteId
    );

    @Modifying
    @Query("""
    UPDATE Atendimento a
    SET a.status = true
    WHERE a.id = :id
      AND a.profissionalId = :profissionalId
""")
    int concluirAtendimento(@Param("id") UUID id, @Param("profissionalId") UUID profissionalId);

}