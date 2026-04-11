package br.org.apae.atendimento.repositories;

import br.org.apae.atendimento.dtos.response.ProfissionalDropdownResponseDTO;
import br.org.apae.atendimento.entities.ProfissionalSaude;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfissionalSaudeRepository extends JpaRepository<ProfissionalSaude, UUID> {

    @Query("SELECT p.nomeCompleto FROM ProfissionalSaude p WHERE p.id = :id")
    String findNomeCompletoById(@Param("id")UUID id);

    Optional<ProfissionalSaude> findByEmailIgnoreCase(String email);

    @Query("SELECT new br.org.apae.atendimento.dtos.response.ProfissionalDropdownResponseDTO(p.id, p.nomeCompleto) " +
    "FROM ProfissionalSaude p WHERE p.status = 'ATIVO' ORDER BY p.nomeCompleto ASC")
    List<ProfissionalDropdownResponseDTO> listarParaDropdown();
}
