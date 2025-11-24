package br.org.apae.atendimento.repositories;

import br.org.apae.atendimento.entities.TipoArquivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoArquivoRepository extends JpaRepository<TipoArquivo, Long> {
}
