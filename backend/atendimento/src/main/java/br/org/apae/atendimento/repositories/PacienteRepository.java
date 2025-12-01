package br.org.apae.atendimento.repositories;

import br.org.apae.atendimento.entities.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

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
}
