package br.org.apae.atendimento.integration;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@ActiveProfiles("test")
public abstract class AbstractPostgresTest {

    private static PostgreSQLContainer<?> postgres;

    @DynamicPropertySource
    static void registerDatasourceProperties(DynamicPropertyRegistry registry) {
        ExternalDatabase externalDatabase = externalDatabase();

        if (externalDatabase != null) {
            registry.add("spring.datasource.url", externalDatabase::url);
            registry.add("spring.datasource.username", externalDatabase::username);
            registry.add("spring.datasource.password", externalDatabase::password);
            registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
            return;
        }

        PostgreSQLContainer<?> container = postgres();
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
        registry.add("spring.datasource.driver-class-name", container::getDriverClassName);
    }

    private static synchronized PostgreSQLContainer<?> postgres() {
        if (postgres == null) {
            postgres = new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("atendimento_test")
                    .withUsername("test")
                    .withPassword("test");
            postgres.start();
            Runtime.getRuntime().addShutdownHook(new Thread(postgres::stop));
        }

        return postgres;
    }

    private static ExternalDatabase externalDatabase() {
        String url = config("TEST_DATABASE_URL");

        if (!hasText(url)) {
            return null;
        }

        String username = firstNonBlank(
                config("TEST_DATABASE_USERNAME"),
                config("TEST_DATABASE_USER"),
                config("DB_USER"),
                config("POSTGRES_USER")
        );
        String password = firstNonBlank(
                config("TEST_DATABASE_PASSWORD"),
                config("DB_PASSWORD"),
                config("POSTGRES_PASSWORD")
        );

        if (!hasText(username) || !hasText(password)) {
            throw new IllegalStateException(
                    "TEST_DATABASE_URL requires a database user and password. " +
                            "Use TEST_DATABASE_USERNAME/TEST_DATABASE_PASSWORD, TEST_DATABASE_USER/TEST_DATABASE_PASSWORD, " +
                            "DB_USER/DB_PASSWORD or POSTGRES_USER/POSTGRES_PASSWORD."
            );
        }

        return new ExternalDatabase(url, username, password);
    }

    private static String config(String key) {
        String property = System.getProperty(key);
        return hasText(property) ? property : System.getenv(key);
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (hasText(value)) {
                return value;
            }
        }

        return null;
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private record ExternalDatabase(String url, String username, String password) {
    }
}
