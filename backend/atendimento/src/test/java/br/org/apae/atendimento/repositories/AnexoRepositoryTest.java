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
    @DisplayName("Deve impedir salvamento quando Título for nulo (NOT NULL)")
    void naoDevePersistirArquivoComTituloNuloViolaConstraint() {
        Arquivo arquivoInvalido = new Arquivo();
        arquivoInvalido.setObjectName(UUID.randomUUID() + "-relatorio.pdf");
        arquivoInvalido.setNomeArquivo("relatorio.pdf");
        arquivoInvalido.setData(LocalDate.now());

        Exception ex = assertThrows(DataIntegrityViolationException.class, () -> repository.saveAndFlush(arquivoInvalido));
        assertTrue(ex.getMessage().toLowerCase().contains("null"));
    }

    @Test
    @DisplayName("Deve lançar exceção ao persistir Título maior que o limite da coluna")
    void naoDevePersistirArquivoComTituloGiganteViolaConstraint() {
        Arquivo arquivoInvalido = new Arquivo();
        arquivoInvalido.setObjectName(UUID.randomUUID() + "-overflow.pdf");
        arquivoInvalido.setNomeArquivo("overflow.pdf");
        arquivoInvalido.setData(LocalDate.now());
        arquivoInvalido.setTitulo("A".repeat(300)); // acima de 255

        Exception ex = assertThrows(DataIntegrityViolationException.class, () -> repository.saveAndFlush(arquivoInvalido));

        String msg = ex.getMessage().toLowerCase();
        assertTrue(msg.contains("value too long") || msg.contains("data exception") || msg.contains("too long") || msg.contains("null"));
    }
}

