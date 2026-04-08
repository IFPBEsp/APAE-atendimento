package br.org.apae.atendimento.repositories;

import br.org.apae.atendimento.entities.Arquivo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class AnexoRepositoryTest {

    @Autowired
    private AnexoRepository repository;

    @Test
    @DisplayName("Deve impedir salvamento no banco quando o Título for nulo (Violação de Constraint NOT NULL)")
    void naoDevePersistirArquivoComTituloNuloViolaConstraint() {

        Arquivo arquivoInvalido = new Arquivo();
        arquivoInvalido.setObjectName(UUID.randomUUID().toString() + "-relatorio.pdf");
        arquivoInvalido.setNomeArquivo("relatorio.pdf");
        arquivoInvalido.setData(LocalDate.now());

        Exception exception = assertThrows(DataIntegrityViolationException.class, () -> {
            repository.saveAndFlush(arquivoInvalido);
        });

        assertTrue(exception.getMessage().toLowerCase().contains("null"));
    }

    @Test
    @DisplayName("Deve lançar exceção ao persistir Título maior que o limite da coluna (VARCHAR)")
    void naoDevePersistirArquivoComTituloGiganteViolaConstraint() {
        Arquivo arquivoInvalido = new Arquivo();
        arquivoInvalido.setObjectName(UUID.randomUUID().toString() + "-overflow.pdf");
        arquivoInvalido.setNomeArquivo("overflow.pdf");
        arquivoInvalido.setData(LocalDate.now());

        arquivoInvalido.setTitulo("A".repeat(300));

        Exception exception = assertThrows(DataIntegrityViolationException.class, () -> {
            repository.saveAndFlush(arquivoInvalido);
        });

        assertTrue(exception.getMessage().toLowerCase().contains("value too long") ||
                exception.getMessage().toLowerCase().contains("data exception") ||
                exception.getMessage().toLowerCase().contains("too long"));
    }
}
