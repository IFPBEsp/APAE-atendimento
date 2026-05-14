package br.org.apae.atendimento.repositories;

import br.org.apae.atendimento.dtos.response.PacienteDropdownResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import br.org.apae.atendimento.entities.views.Paciente;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, UUID> {

    @Query(value = """
        SELECT COUNT(*) > 0
        FROM apae.profissional_paciente pp
        WHERE pp.paciente_id     = :pacienteId
          AND pp.profissional_id = :profissionalId
        """, nativeQuery = true)
    boolean existeRelacao(
            @Param("pacienteId") UUID pacienteId,
            @Param("profissionalId") UUID profissionalId);

    @Query("SELECT p.nomeCompleto FROM Paciente p WHERE p.id = :pacienteId")
    String findNomeCompletoById(@Param("pacienteId") UUID pacienteId);

    // findByProfissionais_Id → substituído por query nativa
    @Query(value = """
        SELECT p.*
        FROM atendimento.vw_pacientes p
        INNER JOIN apae.profissional_paciente pp ON pp.paciente_id = p.paciente_id
        WHERE pp.profissional_id = :profissionalId
        """, nativeQuery = true)
    List<Paciente> findByProfissionalId(@Param("profissionalId") UUID profissionalId);

    @Query(value = """
        SELECT DISTINCT p.*
        FROM atendimento.vw_pacientes p
        INNER JOIN apae.profissional_paciente pp ON pp.paciente_id = p.paciente_id
        LEFT JOIN atendimento.vw_enderecos_paciente e ON e.paciente_id = p.paciente_id
        WHERE pp.profissional_id = :profissionalId
          AND (:nome   IS NULL OR p.nome   ILIKE CONCAT('%', :nome,   '%'))
          AND (:cpf    IS NULL OR p.cpf    LIKE  CONCAT('%', :cpf,    '%'))
          AND (:cidade IS NULL OR e.cidade ILIKE CONCAT('%', :cidade, '%'))
        """, nativeQuery = true)
    List<Paciente> buscarPaciente(
            @Param("profissionalId") UUID profissionalId,
            @Param("nome")   String nome,
            @Param("cpf")    String cpf,
            @Param("cidade") String cidade);

    // listarParaDropdown — is_apagado=false substitui ativo=true
    @Query("""
        SELECT new br.org.apae.atendimento.dtos.response
               .PacienteDropdownResponseDTO(p.id, p.nomeCompleto)
        FROM Paciente p
        WHERE p.isApagado = false
        ORDER BY p.nomeCompleto ASC
        """)
    List<PacienteDropdownResponseDTO> listarParaDropdown();

    boolean existsById(UUID id);
}