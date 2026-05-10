package br.org.apae.atendimento.repositories;

import br.org.apae.atendimento.entities.views.EnderecoPaciente;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnderecoPacienteRepository
        extends JpaRepository<EnderecoPaciente, UUID> {

    Optional<EnderecoPaciente> findByPacienteId(UUID pacienteId);
}
