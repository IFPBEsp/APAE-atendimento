package br.org.apae.atendimento.repositories;


import br.org.apae.atendimento.dtos.response.PacienteDropdownResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import br.org.apae.atendimento.entities.Paciente;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, UUID> {
    @Query(value = """
    SELECT DISTINCT vp.*
    FROM atendimento.vw_pacientes vp
    INNER JOIN atendimento.profissional_paciente pp
            ON pp.paciente_id = vp.paciente_id
    WHERE vp.paciente_id = :id
      AND pp.profissional_id = :profissionalId
""", nativeQuery = true)
    Optional<Paciente> findByIdAndProfissionalId(
            @Param("id") UUID id,
            @Param("profissionalId") UUID profissionalId
    );

    @Query(value = "SELECT CASE WHEN COUNT(1) > 0 THEN true ELSE false END " +
            "FROM atendimento.profissional_paciente pp " +
            "WHERE pp.paciente_id = :pacienteId AND pp.profissional_id = :profissionalId",
            nativeQuery = true)
    boolean existeRelacao(@Param("pacienteId") UUID pacienteId, @Param("profissionalId") UUID profissionalId);

    @Query(value = """
    SELECT vp.nome
    FROM atendimento.vw_pacientes vp
    INNER JOIN atendimento.profissional_paciente pp
            ON pp.paciente_id = vp.paciente_id
    WHERE vp.paciente_id = :pacienteId
      AND pp.profissional_id = :profissionalId
""", nativeQuery = true)
    String findNomeCompletoByIdAndProfissionalId(
            @Param("pacienteId") UUID pacienteId,
            @Param("profissionalId") UUID profissionalId
    );

    @Query(value = """
            SELECT DISTINCT vp.*
            FROM atendimento.vw_pacientes vp
            INNER JOIN atendimento.profissional_paciente pp
                    ON pp.paciente_id = vp.paciente_id
            WHERE pp.profissional_id = :profissionalId
            """,
            nativeQuery = true)
    List<Paciente> findByProfissionalId(@Param("profissionalId") UUID profissionalId);

    @Query(value = "" +
            "SELECT DISTINCT vp.* " +
            "FROM atendimento.vw_pacientes vp " +
            "INNER JOIN atendimento.profissional_paciente pp ON pp.paciente_id = vp.paciente_id " +
            "WHERE pp.profissional_id = :profissionalId " +
            "  AND (:nome IS NULL OR LOWER(vp.nome) LIKE CONCAT('%', LOWER(:nome), '%')) " +
            "  AND (:cpf IS NULL OR vp.cpf LIKE CONCAT('%', :cpf, '%')) " +
            "  AND (:cidade IS NULL OR LOWER(vp.cidade) LIKE CONCAT('%', LOWER(:cidade), '%'))",
            countQuery = "" +
                    "SELECT COUNT(DISTINCT vp.paciente_id) " +
                    "FROM atendimento.vw_pacientes vp " +
                    "INNER JOIN atendimento.profissional_paciente pp ON pp.paciente_id = vp.paciente_id " +
                    "WHERE pp.profissional_id = :profissionalId " +
                    "  AND (:nome IS NULL OR LOWER(vp.nome) LIKE CONCAT('%', LOWER(:nome), '%')) " +
                    "  AND (:cpf IS NULL OR vp.cpf LIKE CONCAT('%', :cpf, '%')) " +
                    "  AND (:cidade IS NULL OR LOWER(vp.cidade) LIKE CONCAT('%', LOWER(:cidade), '%'))",
            nativeQuery = true)
    Page<Paciente> buscarPaciente(
            @Param("profissionalId") UUID profissionalId,
            @Param("nome") String nome,
            @Param("cpf") String cpf,
            @Param("cidade") String cidade,
            Pageable pageable
    );

    @Query("""
    SELECT DISTINCT new br.org.apae.atendimento.dtos.response.PacienteDropdownResponseDTO(p.id, p.nomeCompleto)
    FROM Paciente p
    INNER JOIN ProfissionalPaciente pp
            ON pp.pacienteId = p.id
    WHERE pp.profissionalId = :profissionalId
    ORDER BY p.nomeCompleto ASC
""")
    List<PacienteDropdownResponseDTO> listarParaDropdown(@Param("profissionalId") UUID profissionalId);

    @Query(value = "" +
            "SELECT DISTINCT vp.* " +
            "FROM atendimento.vw_pacientes vp " +
            "WHERE (:nome IS NULL OR LOWER(vp.nome) LIKE CONCAT('%', LOWER(:nome), '%')) " +
            "  AND (:cpf IS NULL OR vp.cpf LIKE CONCAT('%', :cpf, '%')) " +
            "  AND (:cidade IS NULL OR LOWER(vp.cidade) LIKE CONCAT('%', LOWER(:cidade), '%'))",
            countQuery = "" +
                    "SELECT COUNT(DISTINCT vp.paciente_id) " +
                    "FROM atendimento.vw_pacientes vp " +
                    "WHERE (:nome IS NULL OR LOWER(vp.nome) LIKE CONCAT('%', LOWER(:nome), '%')) " +
                    "  AND (:cpf IS NULL OR vp.cpf LIKE CONCAT('%', :cpf, '%')) " +
                    "  AND (:cidade IS NULL OR LOWER(vp.cidade) LIKE CONCAT('%', LOWER(:cidade), '%'))",
            nativeQuery = true)
    Page<Paciente> buscarTodosPacientes(
            @Param("nome") String nome,
            @Param("cpf") String cpf,
            @Param("cidade") String cidade,
            Pageable pageable
    );
}
