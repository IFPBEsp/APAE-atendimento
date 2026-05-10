package br.org.apae.atendimento.repositories;

import br.org.apae.atendimento.entities.views.ResponsavelPaciente;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResponsavelPacienteRepository
        extends JpaRepository<ResponsavelPaciente, UUID> {

    List<ResponsavelPaciente> findAllByPacienteId(UUID pacienteId);
}
