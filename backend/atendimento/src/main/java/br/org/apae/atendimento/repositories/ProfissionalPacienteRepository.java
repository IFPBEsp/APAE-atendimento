package br.org.apae.atendimento.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
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
}