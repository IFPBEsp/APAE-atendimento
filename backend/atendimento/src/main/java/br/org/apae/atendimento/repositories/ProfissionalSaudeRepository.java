package br.org.apae.atendimento.repositories;

import br.org.apae.atendimento.entities.ProfissionalSaude;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfissionalSaudeRepository extends JpaRepository<ProfissionalSaude, Long> {
}
