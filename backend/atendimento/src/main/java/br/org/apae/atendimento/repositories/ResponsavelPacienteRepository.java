package br.org.apae.atendimento.repositories;

import br.org.apae.atendimento.entities.views.ResponsavelPaciente;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResponsavelPacienteRepository
        extends JpaRepository<br.org.apae.atendimento.entities.views.Paciente, UUID> {

    @Query(value = """
        SELECT nome, paciente_id
        FROM atendimento.vw_responsaveis_paciente
        WHERE paciente_id = :pacienteId
        """, nativeQuery = true)
    List<ResponsavelPaciente> findAllByPacienteId(@Param("pacienteId") UUID pacienteId);

    @Query(value = """
    SELECT nome, paciente_id
    FROM atendimento.vw_responsaveis_paciente
    WHERE paciente_id IN (:ids)
    """, nativeQuery = true)
    List<ResponsavelPaciente> findAllByPacienteIdIn(@Param("ids") List<UUID> ids);
}

