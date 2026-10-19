package br.org.apae.atendimento.repositories;

import br.org.apae.atendimento.dtos.response.AgendamentoResponseDTO;
import br.org.apae.atendimento.integration.AbstractPostgresTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(AgendamentoGeralReadRepository.class)
@Sql(scripts = "classpath:db/agendamento_geral_read_repository_test_data.sql")
class AgendamentoGeralReadRepositoryIntegrationTest extends AbstractPostgresTest {

    private static final UUID PROFISSIONAL_COM_AGENDAMENTOS =
            UUID.fromString("d0000000-0000-0000-0000-000000000001");
    private static final UUID PROFISSIONAL_SOMENTE_INATIVO =
            UUID.fromString("d0000000-0000-0000-0000-000000000002");
    private static final UUID PROFISSIONAL_SEM_AGENDAMENTOS =
            UUID.fromString("d0000000-0000-0000-0000-000000000003");
    private static final UUID PACIENTE_ID =
            UUID.fromString("e0000000-0000-0000-0000-000000000001");
    private static final UUID AGENDAMENTO_RECORRENTE_ID =
            UUID.fromString("a0000000-0000-0000-0000-00000000000a");
    private static final UUID AGENDAMENTO_AVULSO_ID =
            UUID.fromString("a0000000-0000-0000-0000-00000000000d");
    private static final UUID AGENDAMENTO_MESMO_DIA_ID =
            UUID.fromString("a0000000-0000-0000-0000-00000000000e");

    @Autowired
    private AgendamentoGeralReadRepository repository;

    @Test
    @DisplayName("Deve filtrar pela data informada e ordenar por horario decrescente para o mesmo dia")
    void deveBuscarAgendamentosDoProfissionalNaDataEspecifica() {
        LocalDate data = LocalDate.of(2030, 3, 8);

        List<AgendamentoResponseDTO> resultado =
                repository.findByProfissionalIdAndDataOrderByDataHoraDesc(PROFISSIONAL_COM_AGENDAMENTOS, data);

        assertEquals(2, resultado.size());

        AgendamentoResponseDTO primeiro = resultado.get(0);
        assertEquals(PACIENTE_ID, primeiro.pacienteId());
        assertEquals("Paciente Repositorio Teste", primeiro.nomePaciente());
        assertEquals(PROFISSIONAL_COM_AGENDAMENTOS, primeiro.profissionalId());
        assertEquals(data, primeiro.data());
        assertEquals(LocalTime.of(14, 0), primeiro.hora());
        assertEquals(md5PseudoUuid(AGENDAMENTO_MESMO_DIA_ID, data), primeiro.id());
        assertEquals("0", primeiro.numeracao());
        assertFalse(primeiro.status());
        assertTrue(primeiro.externo());
        assertNull(primeiro.nomeProfissional());

        AgendamentoResponseDTO segundo = resultado.get(1);
        assertEquals(PACIENTE_ID, segundo.pacienteId());
        assertEquals(data, segundo.data());
        assertEquals(LocalTime.of(10, 30), segundo.hora());
        assertEquals(md5PseudoUuid(AGENDAMENTO_RECORRENTE_ID, data), segundo.id());
        assertTrue(primeiro.hora().isAfter(segundo.hora()));
    }

    @Test
    @DisplayName("Deve buscar agendamento avulso sem data final expandido via coalesce")
    void deveBuscarAgendamentoAvulsoSemDataFinal() {
        LocalDate data = LocalDate.of(2030, 4, 10);

        List<AgendamentoResponseDTO> resultado =
                repository.findByProfissionalIdAndDataOrderByDataHoraDesc(PROFISSIONAL_COM_AGENDAMENTOS, data);

        assertEquals(1, resultado.size());

        AgendamentoResponseDTO dto = resultado.get(0);
        assertEquals(PACIENTE_ID, dto.pacienteId());
        assertEquals(data, dto.data());
        assertEquals(LocalTime.of(16, 0), dto.hora());
        assertEquals(md5PseudoUuid(AGENDAMENTO_AVULSO_ID, data), dto.id());

        assertTrue(repository.findByProfissionalIdAndDataOrderByDataHoraDesc(
                PROFISSIONAL_COM_AGENDAMENTOS, data.plusDays(7)).isEmpty());
    }

    @Test
    @DisplayName("Deve mapear Timestamp para LocalDateTime e gerar UUID determinístico via MD5")
    void deveMapearTiposEGerarUuidViaMd5() {
        LocalDate data = LocalDate.of(2030, 3, 8);

        List<AgendamentoResponseDTO> resultado =
                repository.findByProfissionalIdAndDataOrderByDataHoraDesc(PROFISSIONAL_COM_AGENDAMENTOS, data);
        assertFalse(resultado.isEmpty());

        AgendamentoResponseDTO dto = resultado.get(0);

        assertNotNull(dto.id());
        assertEquals(md5PseudoUuid(AGENDAMENTO_MESMO_DIA_ID, data), dto.id());
        assertEquals(data.atTime(14, 0), dto.data().atTime(dto.hora()));

        UUID idNovamente = repository
                .findByProfissionalIdAndDataOrderByDataHoraDesc(PROFISSIONAL_COM_AGENDAMENTOS, data)
                .get(0).id();
        assertEquals(dto.id(), idNovamente);
    }

    @Test
    @DisplayName("Deve expandir a recorrência e ordenar por data/hora decrescente")
    void deveExpandirRecorrenciaOrdenadoPorDataHoraDesc() {
        List<AgendamentoResponseDTO> resultado =
                repository.findByProfissionalIdOrderByDataHoraDesc(PROFISSIONAL_COM_AGENDAMENTOS);

        assertEquals(5, resultado.size());
        assertEquals(
                List.of(
                        LocalDate.of(2030, 4, 10),
                        LocalDate.of(2030, 3, 15),
                        LocalDate.of(2030, 3, 8),
                        LocalDate.of(2030, 3, 8),
                        LocalDate.of(2030, 3, 1)
                ),
                resultado.stream().map(AgendamentoResponseDTO::data).toList()
        );
        assertEquals(
                List.of(
                        LocalTime.of(16, 0),
                        LocalTime.of(10, 30),
                        LocalTime.of(14, 0),
                        LocalTime.of(10, 30),
                        LocalTime.of(10, 30)
                ),
                resultado.stream().map(AgendamentoResponseDTO::hora).toList()
        );
        assertEquals(5, resultado.stream().map(AgendamentoResponseDTO::id).distinct().count());
    }

    @Test
    @DisplayName("Deve retornar lista vazia para data sem ocorrência de agendamento")
    void deveRetornarListaVaziaParaDataSemAgendamento() {
        assertTrue(repository.findByProfissionalIdAndDataOrderByDataHoraDesc(
                PROFISSIONAL_COM_AGENDAMENTOS, LocalDate.of(2030, 3, 2)).isEmpty());
    }

    @Test
    @DisplayName("Deve retornar lista vazia para profissional sem agendamentos ou inexistente")
    void deveRetornarListaVaziaParaProfissionalSemAgendamentosAtivos() {
        LocalDate data = LocalDate.of(2030, 3, 8);

        assertTrue(repository.findByProfissionalIdAndDataOrderByDataHoraDesc(
                PROFISSIONAL_SEM_AGENDAMENTOS, data).isEmpty());
        assertTrue(repository.findByProfissionalIdAndDataOrderByDataHoraDesc(
                PROFISSIONAL_SOMENTE_INATIVO, data).isEmpty());
        assertTrue(repository.findByProfissionalIdAndDataOrderByDataHoraDesc(
                UUID.randomUUID(), data).isEmpty());
        assertTrue(repository.findByProfissionalIdOrderByDataHoraDesc(PROFISSIONAL_SOMENTE_INATIVO).isEmpty());
    }

    @Test
    @DisplayName("Deve retornar true quando existir agendamento ativo na data expandida e horário informados")
    void deveRetornarTrueQuandoExistirConflitoDeDataEHoraNoSistemaGeral() {
        assertTrue(repository.existsByProfissionalIdAndDataAndHora(
                PROFISSIONAL_COM_AGENDAMENTOS,
                LocalDate.of(2030, 3, 8),
                LocalTime.of(10, 30)
        ));
        assertTrue(repository.existsByProfissionalIdAndDataAndHora(
                PROFISSIONAL_COM_AGENDAMENTOS,
                LocalDate.of(2030, 3, 15),
                LocalTime.of(10, 30)
        ));
        assertTrue(repository.existsByProfissionalIdAndDataAndHora(
                PROFISSIONAL_COM_AGENDAMENTOS,
                LocalDate.of(2030, 3, 8),
                LocalTime.of(14, 0)
        ));
        assertTrue(repository.existsByProfissionalIdAndDataAndHora(
                PROFISSIONAL_COM_AGENDAMENTOS,
                LocalDate.of(2030, 4, 10),
                LocalTime.of(16, 0)
        ));
    }

    @Test
    @DisplayName("Deve retornar false quando horário estiver livre ou agendamento estiver inativo")
    void deveRetornarFalseQuandoHorarioEstiverLivreOuInativo() {
        assertFalse(repository.existsByProfissionalIdAndDataAndHora(
                PROFISSIONAL_COM_AGENDAMENTOS,
                LocalDate.of(2030, 3, 8),
                LocalTime.of(11, 0)
        ));
        assertFalse(repository.existsByProfissionalIdAndDataAndHora(
                PROFISSIONAL_COM_AGENDAMENTOS,
                LocalDate.of(2030, 3, 2),
                LocalTime.of(10, 30)
        ));
        assertFalse(repository.existsByProfissionalIdAndDataAndHora(
                PROFISSIONAL_SOMENTE_INATIVO,
                LocalDate.of(2030, 3, 8),
                LocalTime.of(8, 0)
        ));
        assertFalse(repository.existsByProfissionalIdAndDataAndHora(
                PROFISSIONAL_SEM_AGENDAMENTOS,
                LocalDate.of(2030, 3, 8),
                LocalTime.of(10, 30)
        ));
    }

    private static UUID md5PseudoUuid(UUID agendamentoId, LocalDate data) {
        try {
            byte[] digest = MessageDigest.getInstance("MD5")
                    .digest((agendamentoId.toString() + data).getBytes(StandardCharsets.UTF_8));
            String hex = HexFormat.of().formatHex(digest);
            return UUID.fromString(hex.substring(0, 8) + "-" + hex.substring(8, 12) + "-"
                    + hex.substring(12, 16) + "-" + hex.substring(16, 20) + "-" + hex.substring(20, 32));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
