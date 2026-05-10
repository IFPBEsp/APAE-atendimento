package br.org.apae.atendimento.repositories;

import br.org.apae.atendimento.entities.views.TranstornoPaciente;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TranstornoPacienteRepository
        extends JpaRepository<TranstornoPaciente, UUID> {

    Optional<TranstornoPaciente> findByPacienteId(UUID pacienteId);
}
