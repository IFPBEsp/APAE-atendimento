package br.org.apae.atendimento.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.org.apae.atendimento.entities.ProfissionalPaciente;
import br.org.apae.atendimento.entities.ProfissionalPacienteId;

@Repository
public interface ProfissionalPacienteRepository
        extends JpaRepository<ProfissionalPaciente, ProfissionalPacienteId> {

    boolean existsByProfissionalIdAndPacienteId(
            UUID profissionalId,
            UUID pacienteId
    );

    List<ProfissionalPaciente> findByProfissionalId(UUID profissionalId);

    @Modifying
    @Query(value = """
            INSERT INTO atendimento.profissional_paciente (profissional_id, paciente_id)
            VALUES (:profissionalId, :pacienteId)
            ON CONFLICT DO NOTHING
            """, nativeQuery = true)
    void associarSeNaoExistir(
            @Param("profissionalId") UUID profissionalId,
            @Param("pacienteId") UUID pacienteId
    );
}
