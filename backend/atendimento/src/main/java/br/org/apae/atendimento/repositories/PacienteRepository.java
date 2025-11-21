package br.org.apae.atendimento.repositories;

import br.org.apae.atendimento.entities.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, UUID> {
}
