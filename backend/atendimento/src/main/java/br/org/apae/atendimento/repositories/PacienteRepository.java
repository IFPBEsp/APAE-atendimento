package br.org.apae.atendimento.repositories;


import br.org.apae.atendimento.dtos.response.PacienteDropdownResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import br.org.apae.atendimento.entities.Paciente;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, UUID> {
    @Query("""
    SELECT COUNT(p) > 0 
    FROM Paciente p 
    JOIN p.profissionais prof
    WHERE p.id = :pacienteId
      AND prof.id = :profissionalId
    """)
    boolean existeRelacao(UUID pacienteId, UUID profissionalId);

    @Query("""
        SELECT p.nomeCompleto
        FROM Paciente p
        WHERE p.id = :pacienteId
        """)
    String findNomeCompletoById(@Param("pacienteId") UUID pacienteId);


    List<Paciente>findByProfissionais_Id(UUID profissionalId);
    @Query("""
        SELECT DISTINCT p
        FROM Paciente p
        JOIN p.profissionais prof
        WHERE prof.id = :profissionalId
          AND (:nome IS NULL OR LOWER(p.nomeCompleto) LIKE LOWER(CONCAT('%', :nome, '%')))
          AND (:cpf IS NULL OR p.cpf LIKE CONCAT('%', :cpf, '%'))
          AND (:cidade IS NULL OR LOWER(p.cidade) LIKE LOWER(CONCAT('%', :cidade, '%')))
    """)
    Page<Paciente> buscarPaciente(
            @Param("profissionalId") UUID profissionalId,
            @Param("nome") String nome,
            @Param("cpf") String cpf,
            @Param("cidade") String cidade,
            Pageable pageable
    );

    @Query("SELECT new br.org.apae.atendimento.dtos.response.PacienteDropdownResponseDTO(p.id, p.nomeCompleto) " +
    "FROM Paciente p WHERE p.ativo = true ORDER BY p.nomeCompleto ASC")
    List<PacienteDropdownResponseDTO> listarParaDropdown();
}
