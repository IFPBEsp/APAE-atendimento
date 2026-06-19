package br.org.apae.atendimento.services;

import br.org.apae.atendimento.dtos.request.AgendamentoRequestDTO;
import br.org.apae.atendimento.integration.AbstractIntegrationTest;
import br.org.apae.atendimento.repositories.PacienteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class AgendamentoServiceIntegrationTest extends AbstractIntegrationTest {

    private static final UUID PROFISSIONAL_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private static final UUID PACIENTE_VINCULADO_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID PACIENTE_GERAL_ID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

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

        assertTrue(pacienteRepository.buscarTodosPacientes(null, null, null, org.springframework.data.domain.Pageable.unpaged())
                .stream()
                .anyMatch(paciente -> paciente.getId().equals(PACIENTE_GERAL_ID)));

        assertTrue(pacienteRepository.findByProfissionalId(PROFISSIONAL_ID)
                .stream()
                .anyMatch(paciente -> paciente.getId().equals(PACIENTE_VINCULADO_ID)));

        assertFalse(pacienteRepository.findByProfissionalId(PROFISSIONAL_ID)
                .stream()
                .anyMatch(paciente -> paciente.getId().equals(PACIENTE_GERAL_ID)));
    }

    @Test
    @DisplayName("Deve vincular paciente geral ao profissional quando agendar")
    void deveVincularPacienteGeralAoProfissionalQuandoAgendar() {
        removerVinculoPacienteGeral();

        assertEquals(0, contarVinculosPacienteGeral());

        agendamentoService.agendar(
                new AgendamentoRequestDTO(
                        PACIENTE_GERAL_ID,
                        LocalDate.now().plusDays(10),
                        LocalTime.of(9, 35)
                ),
                PROFISSIONAL_ID
        );

        assertEquals(1, contarVinculosPacienteGeral());
        assertTrue(pacienteRepository.findByProfissionalId(PROFISSIONAL_ID)
                .stream()
                .anyMatch(paciente -> paciente.getId().equals(PACIENTE_GERAL_ID)));
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

    private void removerVinculoPacienteGeral() {
        jdbcTemplate.update(
                "DELETE FROM atendimento.profissional_paciente WHERE profissional_id = ? AND paciente_id = ?",
                PROFISSIONAL_ID,
                PACIENTE_GERAL_ID
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
                "SELECT COUNT(*) FROM atendimento.profissional_paciente WHERE profissional_id = ? AND paciente_id = ?",
                Integer.class,
                PROFISSIONAL_ID,
                pacienteId
        );
        return count == null ? 0 : count;
    }
}
