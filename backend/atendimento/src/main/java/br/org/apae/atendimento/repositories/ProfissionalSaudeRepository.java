package br.org.apae.atendimento.repositories;

import br.org.apae.atendimento.dtos.response.PacienteOptionDTO;
import br.org.apae.atendimento.entities.Paciente;
import br.org.apae.atendimento.entities.ProfissionalSaude;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import br.org.apae.atendimento.entities.ProfissionalSaude;

@Repository
public interface ProfissionalSaudeRepository extends JpaRepository<ProfissionalSaude, UUID> {

    @Query("SELECT p.primeiroNome FROM ProfissionalSaude p WHERE p.id = :id")
    String findPrimeiroNomeById(@Param("id")UUID id);

    Optional<ProfissionalSaude> findByEmail(String email);

    boolean existsByEmail(String email);
}
