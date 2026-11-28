package br.org.apae.atendimento.controllers;

import br.org.apae.atendimento.integration.AbstractIntegrationTest;
import br.org.apae.atendimento.security.UsuarioAutenticado;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class AtendimentoControllerIntegrationTest extends AbstractIntegrationTest {

    private static final String SEED_PROFISSIONAL_ID = "a3000000-0000-4000-8000-000000000001";
    private static final String SEED_PACIENTE_ID = "a4000000-0000-4000-8000-000000000001";
    private static final String SEED_ATENDIMENTO_ID = "a9000000-0000-4000-8000-000000000001";
    private static final String SEED_TOPICO_ID = "aa000000-0000-4000-8000-000000000001";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void cleanupSeedLocalData() {
        jdbcTemplate.update("DELETE FROM atendimento.topico WHERE atendimento_id = ?::uuid", SEED_ATENDIMENTO_ID);
        jdbcTemplate.update("DELETE FROM atendimento.atendimento WHERE id = ?::uuid", SEED_ATENDIMENTO_ID);
        jdbcTemplate.update("DELETE FROM atendimento.agendamento WHERE id = 'a8000000-0000-4000-8000-000000000001'::uuid");
        jdbcTemplate.update("DELETE FROM apae_geral.agendamentos WHERE id = 'a7000000-0000-4000-8000-000000000001'::uuid");
        jdbcTemplate.update("DELETE FROM apae_geral.cadastro_anual_transtorno WHERE cadastro_anual_id IN ('a6000000-0000-4000-8000-000000000001'::uuid, 'a6000000-0000-4000-8000-000000000002'::uuid)");
        jdbcTemplate.update("DELETE FROM apae_geral.cadastros_anuais WHERE id IN ('a6000000-0000-4000-8000-000000000001'::uuid, 'a6000000-0000-4000-8000-000000000002'::uuid)");
        jdbcTemplate.update("DELETE FROM apae_geral.transtornos WHERE id IN ('a5000000-0000-4000-8000-000000000001'::uuid, 'a5000000-0000-4000-8000-000000000002'::uuid)");
        jdbcTemplate.update("DELETE FROM apae_geral.responsaveis WHERE id IN ('a4100000-0000-4000-8000-000000000001'::uuid, 'a4100000-0000-4000-8000-000000000002'::uuid)");
        jdbcTemplate.update("DELETE FROM apae_geral.pacientes WHERE id IN ('a4000000-0000-4000-8000-000000000001'::uuid, 'a4000000-0000-4000-8000-000000000002'::uuid)");
        jdbcTemplate.update("DELETE FROM apae_geral.profissionais_da_saude WHERE id = 'a3000000-0000-4000-8000-000000000001'::uuid");
        jdbcTemplate.update("DELETE FROM apae_geral.usuarios WHERE id = 'a2000000-0000-4000-8000-000000000001'::uuid");
        jdbcTemplate.update("DELETE FROM apae_geral.enderecos WHERE id IN ('a1000000-0000-4000-8000-000000000001'::uuid, 'a1000000-0000-4000-8000-000000000002'::uuid)");
        jdbcTemplate.update("DELETE FROM apae_geral.areas_de_atendimento WHERE area = 'Fisioterapia'");
    }

    private void executarSeedLocal() throws Exception {
        Path seedPath = Path.of(".scripts/seed/local-development.sql");
        if (!Files.exists(seedPath)) {
            seedPath = Path.of("../../.scripts/seed/local-development.sql");
        }
        try (Connection connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.execute("SET search_path TO public, atendimento");
            ScriptUtils.executeSqlScript(connection, new FileSystemResource(seedPath));
        }
    }

    private static UsernamePasswordAuthenticationToken autenticarComo(String profissionalId) {
        return new UsernamePasswordAuthenticationToken(
                new UsuarioAutenticado(UUID.fromString(profissionalId)),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_PROFISSIONAL"))
        );
    }

    @Test
    @DisplayName("Deve criar e listar atendimentos para paciente vinculado ao profissional autenticado com paginação")
    void deveCriarEListarAtendimentosComProfissionalAutenticado() throws Exception {
        String pacienteId = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa";

        String payload = """
        {
          "pacienteId": "%s",
          "relatorio": [{"titulo": "Titulo 1", "descricao": "Descricao 1"}],
          "data": "10-05-2026",
          "hora": "10:00"
        }
        """.formatted(pacienteId);

        mockMvc.perform(
                post("/atendimentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload)
        ).andExpect(status().isCreated());

        // Testando listagem com parâmetros default (page=0, size=10)
        mockMvc.perform(
                get("/atendimentos/{pacienteId}", pacienteId)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.totalElements").isNumber())
        .andExpect(jsonPath("$.totalPages").isNumber())
        .andExpect(jsonPath("$.number").value(0))
        .andExpect(jsonPath("$.size").value(10));

        // Testando listagem com parâmetros explícitos (page=0, size=5)
        mockMvc.perform(
                get("/atendimentos/{pacienteId}", pacienteId)
                        .param("page", "0")
                        .param("size", "5")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.size").value(5))
        .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    @DisplayName("Deve consultar o atendimento do seed local (a9000000-0000-4000-8000-000000000001) retornando HTTP 200 sem NPE e mantendo ordem correta")
    void deveConsultarAtendimentoDoSeedRetornandoHttp200EOrdemCorreta() throws Exception {
        executarSeedLocal();

        Integer ordemPersistida = jdbcTemplate.queryForObject(
                "SELECT ordem FROM atendimento.topico WHERE id = ?::uuid AND atendimento_id = ?::uuid",
                Integer.class,
                SEED_TOPICO_ID,
                SEED_ATENDIMENTO_ID
        );
        assertEquals(0, ordemPersistida);

        mockMvc.perform(
                        get("/atendimentos/{pacienteId}", SEED_PACIENTE_ID)
                                .param("page", "0")
                                .param("size", "10")
                                .with(authentication(autenticarComo(SEED_PROFISSIONAL_ID)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].atendimentos", hasSize(1)))
                .andExpect(jsonPath("$.content[0].atendimentos[0].id").value(SEED_ATENDIMENTO_ID))
                .andExpect(jsonPath("$.content[0].atendimentos[0].numeracao").value("AT-LOCAL-001"))
                .andExpect(jsonPath("$.content[0].atendimentos[0].relatorio", hasSize(1)))
                .andExpect(jsonPath("$.content[0].atendimentos[0].relatorio[0].id").value(SEED_TOPICO_ID))
                .andExpect(jsonPath("$.content[0].atendimentos[0].relatorio[0].titulo").value("Evolucao ficticia"))
                .andExpect(jsonPath("$.content[0].atendimentos[0].relatorio[0].descricao").value("Registro criado exclusivamente para desenvolvimento local."));
    }

    @Test
    @DisplayName("Deve reparar base existente com ordem=1 de forma idempotente ao reexecutar o seed local sem excluir registros")
    void deveRepararBaseExistenteComOrdemIncorretaDeFormaIdempotente() throws Exception {
        executarSeedLocal();

        jdbcTemplate.update(
                "UPDATE atendimento.topico SET ordem = 1 WHERE id = ?::uuid AND atendimento_id = ?::uuid",
                SEED_TOPICO_ID,
                SEED_ATENDIMENTO_ID
        );

        executarSeedLocal();
        executarSeedLocal();

        Integer ordemReparada = jdbcTemplate.queryForObject(
                "SELECT ordem FROM atendimento.topico WHERE id = ?::uuid AND atendimento_id = ?::uuid",
                Integer.class,
                SEED_TOPICO_ID,
                SEED_ATENDIMENTO_ID
        );
        assertEquals(0, ordemReparada);

        Integer totalTopicosAtendimento = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM atendimento.topico WHERE atendimento_id = ?::uuid",
                Integer.class,
                SEED_ATENDIMENTO_ID
        );
        assertEquals(1, totalTopicosAtendimento);

        mockMvc.perform(
                        get("/atendimentos/{pacienteId}", SEED_PACIENTE_ID)
                                .param("page", "0")
                                .param("size", "10")
                                .with(authentication(autenticarComo(SEED_PROFISSIONAL_ID)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].atendimentos[0].id").value(SEED_ATENDIMENTO_ID))
                .andExpect(jsonPath("$.content[0].atendimentos[0].relatorio", hasSize(1)))
                .andExpect(jsonPath("$.content[0].atendimentos[0].relatorio[0].id").value(SEED_TOPICO_ID));
    }

    @Test
    @DisplayName("Deve evitar NullPointerException (HTTP 500) filtrando defensivamente elementos nulos quando houver lacuna na OrderColumn")
    void deveEvitarNpeQuandoHouverLacunaNaOrderColumn() throws Exception {
        executarSeedLocal();

        jdbcTemplate.update(
                "UPDATE atendimento.topico SET ordem = 1 WHERE id = ?::uuid AND atendimento_id = ?::uuid",
                SEED_TOPICO_ID,
                SEED_ATENDIMENTO_ID
        );

        mockMvc.perform(
                        get("/atendimentos/{pacienteId}", SEED_PACIENTE_ID)
                                .param("page", "0")
                                .param("size", "10")
                                .with(authentication(autenticarComo(SEED_PROFISSIONAL_ID)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].atendimentos[0].id").value(SEED_ATENDIMENTO_ID))
                .andExpect(jsonPath("$.content[0].atendimentos[0].relatorio", hasSize(1)))
                .andExpect(jsonPath("$.content[0].atendimentos[0].relatorio[0].id").value(SEED_TOPICO_ID))
                .andExpect(jsonPath("$.content[0].atendimentos[0].relatorio[0].titulo").value("Evolucao ficticia"));
    }

    @Test
    @DisplayName("Deve preservar os tópicos e a ordem após editar o atendimento fictício e reexecutar o seed local")
    void devePreservarTopicosEditadosDoAtendimentoFicticioAoReexecutarSeedLocal() throws Exception {
        executarSeedLocal();

        String payloadEdicao = """
        {
          "pacienteId": "%s",
          "relatorio": [
            {"titulo": "Primeiro topico editado", "descricao": "Descricao editada 1"},
            {"titulo": "Segundo topico editado", "descricao": "Descricao editada 2"}
          ],
          "data": "10-05-2026",
          "hora": "09:00"
        }
        """.formatted(SEED_PACIENTE_ID);

        mockMvc.perform(
                        put("/atendimentos/{atendimentoId}", SEED_ATENDIMENTO_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payloadEdicao)
                                .with(authentication(autenticarComo(SEED_PROFISSIONAL_ID)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.relatorio", hasSize(2)))
                .andExpect(jsonPath("$.relatorio[0].titulo").value("Primeiro topico editado"))
                .andExpect(jsonPath("$.relatorio[1].titulo").value("Segundo topico editado"));

        executarSeedLocal();

        List<Integer> ordensPersistidas = jdbcTemplate.queryForList(
                "SELECT ordem FROM atendimento.topico WHERE atendimento_id = ?::uuid ORDER BY ordem ASC",
                Integer.class,
                SEED_ATENDIMENTO_ID
        );
        assertEquals(List.of(0, 1), ordensPersistidas);

        List<String> titulosPersistidos = jdbcTemplate.queryForList(
                "SELECT titulo FROM atendimento.topico WHERE atendimento_id = ?::uuid ORDER BY ordem ASC",
                String.class,
                SEED_ATENDIMENTO_ID
        );
        assertEquals(List.of("Primeiro topico editado", "Segundo topico editado"), titulosPersistidos);

        mockMvc.perform(
                        get("/atendimentos/{pacienteId}", SEED_PACIENTE_ID)
                                .param("page", "0")
                                .param("size", "10")
                                .with(authentication(autenticarComo(SEED_PROFISSIONAL_ID)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].atendimentos[0].id").value(SEED_ATENDIMENTO_ID))
                .andExpect(jsonPath("$.content[0].atendimentos[0].relatorio", hasSize(2)))
                .andExpect(jsonPath("$.content[0].atendimentos[0].relatorio[0].titulo").value("Primeiro topico editado"))
                .andExpect(jsonPath("$.content[0].atendimentos[0].relatorio[1].titulo").value("Segundo topico editado"));
    }
}
