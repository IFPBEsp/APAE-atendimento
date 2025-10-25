package br.org.apae.atendimento.repositories;

import br.org.apae.atendimento.entities.ProfissionalSaude;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfissionalSaudeRepository extends JpaRepository<ProfissionalSaude, Long> {

    @Query("SELECT p.primeiroNome FROM ProfissionalSaude p WHERE p.id = :id")
    String findPrimeiroNomeById(@Param("id")Long id);
}
