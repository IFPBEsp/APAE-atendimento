package br.org.apae.atendimento.integration;

import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Sql(
        scripts = "classpath:db/cleanup_atendimento_test_data.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
public abstract class AbstractIntegrationTest extends AbstractPostgresTest {
}
