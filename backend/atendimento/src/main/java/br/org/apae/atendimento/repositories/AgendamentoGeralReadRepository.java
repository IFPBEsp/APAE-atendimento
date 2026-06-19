package br.org.apae.atendimento.repositories;

import br.org.apae.atendimento.dtos.response.AgendamentoResponseDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class AgendamentoGeralReadRepository {

    private final JdbcTemplate jdbcTemplate;

    public AgendamentoGeralReadRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<AgendamentoResponseDTO> findByProfissionalIdOrderByDataHora(UUID profissionalId) {
        String sql = """
                SELECT
                    (
                        SUBSTR(MD5(a.id::text || gs.data::date::text), 1, 8) || '-' ||
                        SUBSTR(MD5(a.id::text || gs.data::date::text), 9, 4) || '-' ||
                        SUBSTR(MD5(a.id::text || gs.data::date::text), 13, 4) || '-' ||
                        SUBSTR(MD5(a.id::text || gs.data::date::text), 17, 4) || '-' ||
                        SUBSTR(MD5(a.id::text || gs.data::date::text), 21, 12)
                    )::uuid AS id,
                    ca.paciente_id,
                    p.nome_completo AS nome_paciente,
                    (gs.data::date + a.hora) AS data_hora,
                    FALSE AS status
                FROM apae_geral.agendamentos a
                INNER JOIN apae_geral.cadastros_anuais ca
                        ON ca.id = a.cadastro_anual_id
                INNER JOIN apae_geral.pacientes p
                        ON p.id = ca.paciente_id
                CROSS JOIN LATERAL generate_series(
                        a.data_inicial,
                        COALESCE(a.data_final, a.data_inicial),
                        GREATEST(COALESCE(a.frequencia_dias, 1), 1) * INTERVAL '1 day'
                ) AS gs(data)
                WHERE a.profissional_id = ?
                  AND a.ativo = TRUE
                ORDER BY data_hora
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            LocalDateTime dataHora = rs.getObject("data_hora", LocalDateTime.class);
            if (dataHora == null) {
                Timestamp timestamp = rs.getTimestamp("data_hora");
                dataHora = timestamp != null ? timestamp.toLocalDateTime() : null;
            }

            return new AgendamentoResponseDTO(
                    rs.getObject("id", UUID.class),
                    rs.getObject("paciente_id", UUID.class),
                    rs.getString("nome_paciente"),
                    dataHora.toLocalDate(),
                    dataHora.toLocalTime(),
                    "0",
                    rs.getBoolean("status"),
                    true
            );
        }, profissionalId);
    }
}
