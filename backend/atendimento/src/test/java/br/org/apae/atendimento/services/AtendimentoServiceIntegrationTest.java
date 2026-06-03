package br.org.apae.atendimento.services;

import br.org.apae.atendimento.dtos.request.AtendimentoRequestDTO;
import br.org.apae.atendimento.dtos.request.TopicoRequestDTO;
import br.org.apae.atendimento.dtos.response.AtendimentoResponseDTO;
import br.org.apae.atendimento.dtos.response.MesAnoAtendimentoResponseDTO;
import br.org.apae.atendimento.entities.Paciente;
import br.org.apae.atendimento.entities.ProfissionalSaude;
import br.org.apae.atendimento.repositories.AtendimentoRepository;
import br.org.apae.atendimento.repositories.PacienteRepository;
import br.org.apae.atendimento.repositories.ProfissionalSaudeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AtendimentoServiceIntegrationTest {

    @Autowired
    private AtendimentoService atendimentoService;

    @Autowired
    private AtendimentoRepository atendimentoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private ProfissionalSaudeRepository profissionalSaudeRepository;

    @Test
    @DisplayName("Deve criar, listar e editar atendimentos mantendo numeração por mês/ano")
    void deveCriarListarEEditarComNumeracaoPorMesEAno() {
        ProfissionalSaude profissional = profissionalSaudeRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new AssertionError("Nenhum profissional carregado no data-test.sql"));

        Paciente paciente = pacienteRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new AssertionError("Nenhum paciente carregado no data-test.sql"));

        if (!pacienteRepository.existeRelacao(paciente.getId(), profissional.getId())) {
            fail("O paciente de teste precisa estar vinculado ao profissional no data-test.sql");
        }

        AtendimentoRequestDTO request1 = new AtendimentoRequestDTO(
                paciente.getId(),
                List.of(new TopicoRequestDTO("Título 1", "Descrição 1")),
                LocalDate.of(2026, 5, 10),
                LocalTime.of(10, 0)
        );

        AtendimentoRequestDTO request2 = new AtendimentoRequestDTO(
                paciente.getId(),
                List.of(new TopicoRequestDTO("Título 2", "Descrição 2")),
                LocalDate.of(2026, 5, 11),
                LocalTime.of(11, 0)
        );

        AtendimentoResponseDTO created1 = atendimentoService.addAtendimento(request1, profissional.getId());
        AtendimentoResponseDTO created2 = atendimentoService.addAtendimento(request2, profissional.getId());

        assertNotNull(created1);
        assertNotNull(created2);
        assertEquals("1", created1.numeracao());
        assertEquals("2", created2.numeracao());

        AtendimentoRequestDTO editRequest = new AtendimentoRequestDTO(
                paciente.getId(),
                List.of(new TopicoRequestDTO("Título editado", "Descrição editada")),
                LocalDate.of(2026, 5, 15),
                LocalTime.of(14, 0)
        );

        AtendimentoResponseDTO edited = atendimentoService.editar(
                editRequest,
                created1.id(),
                profissional.getId()
        );

        assertNotNull(edited);
        assertEquals(created1.numeracao(), edited.numeracao());

        List<MesAnoAtendimentoResponseDTO> agrupados =
                atendimentoService.getAtendimentosAgrupadosPorMes(paciente.getId(), profissional.getId());

        assertFalse(agrupados.isEmpty());
        assertTrue(agrupados.stream().anyMatch(g -> g.mesAno().equals(YearMonth.of(2026, 5))));
    }
}