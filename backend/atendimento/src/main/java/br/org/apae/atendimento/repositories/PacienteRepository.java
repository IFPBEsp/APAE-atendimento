package br.org.apae.atendimento.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
        SELECT p
        FROM Paciente p
        WHERE (:nome IS NULL OR LOWER(p.nomeCompleto) LIKE LOWER(CONCAT('%', :nome, '%')))
        AND (:cpf IS NULL OR p.cpf LIKE CONCAT('%', :cpf, '%'))
        AND (:cidade IS NULL OR LOWER(p.cidade) LIKE LOWER(CONCAT('%', :cidade, '%')))
        """)
    List<Paciente> buscarPaciente(@Param("nome") String nome, @Param("cpf") String cpf, @Param("cidade") String cidade);
}
