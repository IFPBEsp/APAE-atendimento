package br.org.apae.atendimento.integration;

import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public abstract class AbstractIntegrationTest extends AbstractPostgresTest {
}