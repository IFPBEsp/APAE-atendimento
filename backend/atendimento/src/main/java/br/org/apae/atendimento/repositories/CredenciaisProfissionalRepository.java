package br.org.apae.atendimento.repositories;

import br.org.apae.atendimento.entities.CredenciaisProfissional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CredenciaisProfissionalRepository
        extends JpaRepository<CredenciaisProfissional, UUID> {

}