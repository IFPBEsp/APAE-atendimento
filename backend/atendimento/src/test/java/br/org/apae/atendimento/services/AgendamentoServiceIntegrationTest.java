package br.org.apae.atendimento.services;

import br.org.apae.atendimento.dtos.request.AgendamentoRequestDTO;
import br.org.apae.atendimento.dtos.response.AgendamentoResponseDTO;
import br.org.apae.atendimento.dtos.response.DiaAgendamentoResponseDTO;
import br.org.apae.atendimento.exceptions.invalid.AgendamentoInvalidException;
import br.org.apae.atendimento.exceptions.invalid.RelacaoInvalidException;
import br.org.apae.atendimento.integration.AbstractIntegrationTest;
import br.org.apae.atendimento.repositories.PacienteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class AgendamentoServiceIntegrationTest extends AbstractIntegrationTest {

    private static final UUID PROFISSIONAL_ID =
            UUID.fromString("44444444-4444-4444-4444-444444444444");

    private static final UUID PACIENTE_VINCULADO_ID =
            UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

    private static final UUID PACIENTE_GERAL_ID =
            UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

    @Autowired
    private AgendamentoService agendamentoService;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Deve separar pacientes gerais de pacientes vinculados")
    void deveSepararPacientesGeraisDePacientesVinculados() {
        removerVinculoPacienteGeral();

        assertTrue(
                pacienteRepository.buscarPaciente(PROFISSIONAL_ID,
                        null,
                        null,
                        null,
                        org.springframework.data.domain.Pageable.unpaged()
                )
                .stream()
                .anyMatch(paciente ->
                        paciente.getId().equals(PACIENTE_GERAL_ID)
                )
        );

        assertTrue(
                pacienteRepository.findByProfissionalId(PROFISSIONAL_ID)
                        .stream()
                        .anyMatch(paciente ->
                                paciente.getId().equals(PACIENTE_VINCULADO_ID)
                        )
        );

        assertFalse(
                pacienteRepository.findByProfissionalId(PROFISSIONAL_ID)
                        .stream()
                        .anyMatch(paciente ->
                                paciente.getId().equals(PACIENTE_GERAL_ID)
                        )
        );
    }

    @Test
    @DisplayName("Agendamento local não cria vínculo com paciente sem agenda no Geral")
    void agendamentoLocalNaoCriaVinculo() {
        assertEquals(0, contarVinculosPacienteGeral());

        assertThrows(
                RelacaoInvalidException.class,
                () -> agendamentoService.agendar(
                        new AgendamentoRequestDTO(
                                PACIENTE_GERAL_ID,
                                LocalDate.now().plusDays(10),
                                LocalTime.of(9, 35)
                        ),
                        PROFISSIONAL_ID
                )
        );

        assertEquals(0, contarVinculosPacienteGeral());

        assertFalse(
                pacienteRepository.findByProfissionalId(PROFISSIONAL_ID)
                        .stream()
                        .anyMatch(paciente ->
                                paciente.getId().equals(PACIENTE_GERAL_ID)
                        )
        );
    }

    @Test
    @DisplayName("A view deduplica agendas do Geral")
    void viewUsaSomenteHistoricoDoGeral() {
        assertEquals(0, contarVinculosPacienteGeral());

        criarAgendaNoGeralParaPacienteGeral();
        criarAgendaNoGeralParaPacienteGeral();

        assertEquals(1, contarVinculosPacienteGeral());
        assertTrue(
                pacienteRepository.existeRelacao(
                        PACIENTE_GERAL_ID,
                        PROFISSIONAL_ID
                )
        );
    }

    @Test
    @DisplayName("Nao deve duplicar vinculo ao agendar paciente ja vinculado")
    void naoDeveDuplicarVinculoAoAgendarPacienteJaVinculado() {
        assertEquals(1, contarVinculosPacienteVinculado());

        agendamentoService.agendar(
                new AgendamentoRequestDTO(
                        PACIENTE_VINCULADO_ID,
                        LocalDate.now().plusDays(11),
                        LocalTime.of(10, 40)
                ),
                PROFISSIONAL_ID
        );

        assertEquals(1, contarVinculosPacienteVinculado());
    }

    @Test
    @DisplayName("Deve editar data e hora de um agendamento existente")
    void deveEditarDataEHoraDoAgendamento() {
        LocalDate dataOriginal = LocalDate.now().plusDays(5);
        LocalTime horaOriginal = LocalTime.of(8, 0);

        AgendamentoResponseDTO criado = agendamentoService.agendar(
                new AgendamentoRequestDTO(
                        PACIENTE_VINCULADO_ID,
                        dataOriginal,
                        horaOriginal
                ),
                PROFISSIONAL_ID
        );

        LocalDate novaData = LocalDate.now().plusDays(6);
        LocalTime novaHora = LocalTime.of(14, 30);

        AgendamentoResponseDTO editado = agendamentoService.editar(
                criado.id(),
                PROFISSIONAL_ID,
                new AgendamentoRequestDTO(
                        PACIENTE_VINCULADO_ID,
                        novaData,
                        novaHora
                )
        );

        assertEquals(criado.id(), editado.id());
        assertEquals(novaData, editado.data());
        assertEquals(novaHora, editado.hora());
        assertEquals(PACIENTE_VINCULADO_ID, editado.pacienteId());
    }

    @Test
    @DisplayName("Deve editar agendamento apenas após o Geral criar o vínculo")
    void deveEditarPacienteDoAgendamentoComSucesso() {
        removerVinculoPacienteGeral();

        assertEquals(0, contarVinculosPacienteGeral());

        LocalDate data = LocalDate.now().plusDays(7);
        LocalTime hora = LocalTime.of(10, 0);

        AgendamentoResponseDTO criado = agendamentoService.agendar(
                new AgendamentoRequestDTO(
                        PACIENTE_VINCULADO_ID,
                        data,
                        hora
                ),
                PROFISSIONAL_ID
        );

        assertEquals(PACIENTE_VINCULADO_ID, criado.pacienteId());

        assertThrows(
                RelacaoInvalidException.class,
                () -> agendamentoService.editar(
                        criado.id(),
                        PROFISSIONAL_ID,
                        new AgendamentoRequestDTO(
                                PACIENTE_GERAL_ID,
                                data,
                                hora
                        )
                )
        );

        criarAgendaNoGeralParaPacienteGeral();

        assertEquals(1, contarVinculosPacienteGeral());

        AgendamentoResponseDTO editado = agendamentoService.editar(
                criado.id(),
                PROFISSIONAL_ID,
                new AgendamentoRequestDTO(
                        PACIENTE_GERAL_ID,
                        data,
                        hora
                )
        );

        assertEquals(criado.id(), editado.id());
        assertEquals(PACIENTE_GERAL_ID, editado.pacienteId());
        assertEquals("Lucas Souza", editado.nomePaciente());
        assertEquals(1, contarVinculosPacienteGeral());
    }

    @Test
    @DisplayName("Nao deve permitir editar agendamento que ja foi concluido")
    void naoDevePermitirEditarAgendamentoConcluido() {
        LocalDate data = LocalDate.now().plusDays(8);
        LocalTime hora = LocalTime.of(11, 0);

        AgendamentoResponseDTO criado = agendamentoService.agendar(
                new AgendamentoRequestDTO(
                        PACIENTE_VINCULADO_ID,
                        data,
                        hora
                ),
                PROFISSIONAL_ID
        );

        agendamentoService.concluir(
                PROFISSIONAL_ID,
                PACIENTE_VINCULADO_ID,
                criado.id()
        );

        assertThrows(
                AgendamentoInvalidException.class,
                () -> agendamentoService.editar(
                        criado.id(),
                        PROFISSIONAL_ID,
                        new AgendamentoRequestDTO(
                                PACIENTE_VINCULADO_ID,
                                data.plusDays(1),
                                LocalTime.of(15, 0)
                        )
                )
        );
    }

    @Test
    @DisplayName("Deve manter metadados consistentes ao paginar agendamentos agrupados por dia")
    void deveManterTotalElementsConsistenteAoPaginarAgendamentosAgrupados() {
        removerVinculoPacienteGeral();
        criarAgendaNoGeralParaPacienteGeral();

        LocalDate data1 = LocalDate.now().plusDays(40);
        LocalDate data2 = LocalDate.now().plusDays(41);
        LocalDate data3 = LocalDate.now().plusDays(42);
        LocalDate data4 = LocalDate.now().plusDays(43);
        LocalDate data5 = LocalDate.now().plusDays(44);
        LocalDate data6 = LocalDate.now().plusDays(45);
        LocalDate data7 = LocalDate.now().plusDays(46);
        LocalDate data8 = LocalDate.now().plusDays(47);
        LocalDate data9 = LocalDate.now().plusDays(48);
        LocalDate data10 = LocalDate.now().plusDays(49);
        LocalDate data11 = LocalDate.now().plusDays(50);
        LocalDate data12 = LocalDate.now().plusDays(51);
        LocalDate data13 = LocalDate.now().plusDays(52);
        LocalDate data14 = LocalDate.now().plusDays(53);

        agendamentoService.agendar(new AgendamentoRequestDTO(PACIENTE_VINCULADO_ID, data14, LocalTime.of(9, 0)), PROFISSIONAL_ID);
        agendamentoService.agendar(new AgendamentoRequestDTO(PACIENTE_VINCULADO_ID, data13, LocalTime.of(9, 0)), PROFISSIONAL_ID);
        agendamentoService.agendar(new AgendamentoRequestDTO(PACIENTE_VINCULADO_ID, data12, LocalTime.of(9, 0)), PROFISSIONAL_ID);
        agendamentoService.agendar(new AgendamentoRequestDTO(PACIENTE_VINCULADO_ID, data11, LocalTime.of(9, 0)), PROFISSIONAL_ID);
        agendamentoService.agendar(new AgendamentoRequestDTO(PACIENTE_VINCULADO_ID, data10, LocalTime.of(9, 0)), PROFISSIONAL_ID);
        agendamentoService.agendar(new AgendamentoRequestDTO(PACIENTE_VINCULADO_ID, data9, LocalTime.of(9, 0)), PROFISSIONAL_ID);
        agendamentoService.agendar(new AgendamentoRequestDTO(PACIENTE_VINCULADO_ID, data8, LocalTime.of(9, 0)), PROFISSIONAL_ID);
        agendamentoService.agendar(new AgendamentoRequestDTO(PACIENTE_VINCULADO_ID, data7, LocalTime.of(9, 0)), PROFISSIONAL_ID);
        agendamentoService.agendar(new AgendamentoRequestDTO(PACIENTE_VINCULADO_ID, data6, LocalTime.of(9, 0)), PROFISSIONAL_ID);
        agendamentoService.agendar(new AgendamentoRequestDTO(PACIENTE_VINCULADO_ID, data5, LocalTime.of(9, 0)), PROFISSIONAL_ID);

        agendamentoService.agendar(new AgendamentoRequestDTO(PACIENTE_VINCULADO_ID, data4, LocalTime.of(9, 0)), PROFISSIONAL_ID);
        agendamentoService.agendar(new AgendamentoRequestDTO(PACIENTE_GERAL_ID, data4, LocalTime.of(10, 0)), PROFISSIONAL_ID);
        agendamentoService.agendar(new AgendamentoRequestDTO(PACIENTE_VINCULADO_ID, data3, LocalTime.of(9, 0)), PROFISSIONAL_ID);
        agendamentoService.agendar(new AgendamentoRequestDTO(PACIENTE_GERAL_ID, data3, LocalTime.of(10, 0)), PROFISSIONAL_ID);
        agendamentoService.agendar(new AgendamentoRequestDTO(PACIENTE_VINCULADO_ID, data2, LocalTime.of(9, 0)), PROFISSIONAL_ID);
        agendamentoService.agendar(new AgendamentoRequestDTO(PACIENTE_GERAL_ID, data1, LocalTime.of(10, 0)), PROFISSIONAL_ID);

        Page<DiaAgendamentoResponseDTO> pagina =
                agendamentoService.listarAgrupadoPorDia(
                        PROFISSIONAL_ID,
                        null,
                        1,
                        10
                );

        assertEquals(4, pagina.getContent().size());
        assertEquals(16, pagina.getTotalElements());
        assertEquals(2, pagina.getTotalPages());
        assertFalse(pagina.isFirst());
        assertTrue(pagina.isLast());
    }

    private void removerVinculoPacienteGeral() {
        jdbcTemplate.update(
                "DELETE FROM atendimento.profissional_paciente " +
                        "WHERE profissional_id = ? AND paciente_id = ?",
                PROFISSIONAL_ID,
                PACIENTE_GERAL_ID
        );
    }

    private void criarAgendaNoGeralParaPacienteGeral() {
        jdbcTemplate.update(
                "INSERT INTO apae_geral.agendamentos " +
                        "(id, cadastro_anual_id, profissional_id, frequencia_dias, hora, data_inicial, ativo) " +
                        "VALUES (?, ?, ?, 7, TIME '10:00', DATE '2026-01-01', FALSE)",
                UUID.randomUUID(),
                UUID.fromString("66666666-6666-6666-6666-666666666662"),
                PROFISSIONAL_ID
        );
    }

    private int contarVinculosPacienteGeral() {
        return contarVinculos(PACIENTE_GERAL_ID);
    }

    private int contarVinculosPacienteVinculado() {
        return contarVinculos(PACIENTE_VINCULADO_ID);
    }

    private int contarVinculos(UUID pacienteId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM atendimento.profissional_paciente " +
                        "WHERE profissional_id = ? AND paciente_id = ?",
                Integer.class,
                PROFISSIONAL_ID,
                pacienteId
        );

        return count == null ? 0 : count;
    }
}