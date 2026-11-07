package br.org.apae.atendimento.services;

import br.org.apae.atendimento.dtos.request.AtendimentoRequestDTO;
import br.org.apae.atendimento.dtos.request.TopicoRequestDTO;
import br.org.apae.atendimento.dtos.response.AtendimentoResponseDTO;
import br.org.apae.atendimento.dtos.response.MesAnoAtendimentoResponseDTO;
import br.org.apae.atendimento.dtos.response.TopicoResponseDTO;
import br.org.apae.atendimento.entities.Paciente;
import br.org.apae.atendimento.entities.ProfissionalSaude;
import br.org.apae.atendimento.exceptions.invalid.RelacaoInvalidException;
import br.org.apae.atendimento.integration.AbstractIntegrationTest;
import br.org.apae.atendimento.repositories.PacienteRepository;
import br.org.apae.atendimento.repositories.ProfissionalSaudeRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class AtendimentoServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private AtendimentoService atendimentoService;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private ProfissionalSaudeRepository profissionalSaudeRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Deve criar, listar e editar atendimentos mantendo numeracao por mes/ano")
    void deveCriarListarEEditarComNumeracaoPorMesEAno() {
        ProfissionalSaude profissional = profissionalSaudeRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new AssertionError("Nenhum profissional carregado pelas migrations de teste"));

        Paciente paciente = pacienteRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new AssertionError("Nenhum paciente carregado pelas migrations de teste"));

        if (!pacienteRepository.existeRelacao(paciente.getId(), profissional.getId())) {
            fail("O paciente de teste precisa estar vinculado ao profissional em atendimento.profissional_paciente");
        }

        AtendimentoRequestDTO request1 = new AtendimentoRequestDTO(
                paciente.getId(),
                List.of(new TopicoRequestDTO("Titulo 1", "Descricao 1")),
                LocalDate.of(2026, 5, 10),
                LocalTime.of(10, 0),
                null
        );

        AtendimentoRequestDTO request2 = new AtendimentoRequestDTO(
                paciente.getId(),
                List.of(new TopicoRequestDTO("Titulo 2", "Descricao 2")),
                LocalDate.of(2026, 5, 11),
                LocalTime.of(11, 0),
                null
        );

        AtendimentoResponseDTO created1 = atendimentoService.addAtendimento(request1, profissional.getId());
        AtendimentoResponseDTO created2 = atendimentoService.addAtendimento(request2, profissional.getId());

        assertNotNull(created1);
        assertNotNull(created2);
        assertEquals("1", created1.numeracao());
        assertEquals("2", created2.numeracao());

        AtendimentoRequestDTO editRequest = new AtendimentoRequestDTO(
                paciente.getId(),
                List.of(new TopicoRequestDTO("Titulo editado", "Descricao editada")),
                LocalDate.of(2026, 5, 15),
                LocalTime.of(14, 0),
                null
        );

        AtendimentoResponseDTO edited = atendimentoService.editar(
                editRequest,
                created1.id(),
                profissional.getId()
        );

        assertNotNull(edited);
        assertEquals(created1.numeracao(), edited.numeracao());

        Page<MesAnoAtendimentoResponseDTO> agrupados =
                atendimentoService.getAtendimentosAgrupadosPorMes(paciente.getId(), profissional.getId(), 0, 10);

        assertNotNull(agrupados);
        assertFalse(agrupados.isEmpty());
        assertEquals(1, agrupados.getTotalElements());
        assertEquals(2, agrupados.getContent().getFirst().atendimentos().size());
        assertEquals(1, agrupados.getTotalPages());
        assertTrue(agrupados.getContent().stream().anyMatch(g -> g.mesAno().equals(YearMonth.of(2026, 5))));
    }

    @Test
    @DisplayName("Nao deve criar relacao entre paciente e profissional ao criar atendimento")
    void naoDeveCriarRelacaoAoCriarAtendimento() {
        ProfissionalSaude profissional = profissionalSaudeRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new AssertionError("Nenhum profissional carregado pelas migrations de teste"));

        Paciente paciente = pacienteRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new AssertionError("Nenhum paciente carregado pelas migrations de teste"));

        jdbcTemplate.update(
                "DELETE FROM apae_geral.agendamentos WHERE profissional_id = ?",
                profissional.getId()
        );

        assertFalse(pacienteRepository.existeRelacao(paciente.getId(), profissional.getId()));

        AtendimentoRequestDTO request = new AtendimentoRequestDTO(
                paciente.getId(),
                List.of(new TopicoRequestDTO("Titulo", "Descricao")),
                LocalDate.of(2026, 6, 10),
                LocalTime.of(9, 0),
                null
        );

        assertThrows(RelacaoInvalidException.class,
                () -> atendimentoService.addAtendimento(request, profissional.getId()));
        assertFalse(pacienteRepository.existeRelacao(paciente.getId(), profissional.getId()));
    }

    @Test
    @DisplayName("Deve preservar a ordem contígua iniciada em 0 após criação, edição, remoção de tópicos e recarregamento do banco")
    void devePreservarOrdemContiguaAposCriacaoEdicaoERemocaoDeTopicos() {
        UUID profissionalId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        UUID pacienteId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

        AtendimentoRequestDTO criacao = new AtendimentoRequestDTO(
                pacienteId,
                List.of(
                        new TopicoRequestDTO("Topico 1", "Descricao 1"),
                        new TopicoRequestDTO("Topico 2", "Descricao 2"),
                        new TopicoRequestDTO("Topico 3", "Descricao 3")
                ),
                LocalDate.of(2026, 7, 10),
                LocalTime.of(9, 0),
                null
        );

        AtendimentoResponseDTO criado = atendimentoService.addAtendimento(criacao, profissionalId);
        entityManager.flush();
        entityManager.clear();

        List<Integer> ordensAposCriacao = jdbcTemplate.queryForList(
                "SELECT ordem FROM atendimento.topico WHERE atendimento_id = ? ORDER BY ordem ASC",
                Integer.class,
                criado.id()
        );
        assertEquals(List.of(0, 1, 2), ordensAposCriacao);

        Page<MesAnoAtendimentoResponseDTO> recarregadoCriacao =
                atendimentoService.getAtendimentosAgrupadosPorMes(pacienteId, profissionalId, 0, 10);
        List<String> titulosCriacao = recarregadoCriacao.getContent().getFirst().atendimentos().getFirst()
                .relatorio().stream().map(TopicoResponseDTO::titulo).toList();
        assertEquals(List.of("Topico 1", "Topico 2", "Topico 3"), titulosCriacao);

        AtendimentoRequestDTO edicao = new AtendimentoRequestDTO(
                pacienteId,
                List.of(
                        new TopicoRequestDTO("Topico 3 Editado", "Descricao 3 Editada"),
                        new TopicoRequestDTO("Topico 1 Mantido", "Descricao 1 Mantida")
                ),
                LocalDate.of(2026, 7, 10),
                LocalTime.of(9, 0),
                null
        );

        atendimentoService.editar(edicao, criado.id(), profissionalId);
        entityManager.flush();
        entityManager.clear();

        List<Integer> ordensAposEdicao = jdbcTemplate.queryForList(
                "SELECT ordem FROM atendimento.topico WHERE atendimento_id = ? ORDER BY ordem ASC",
                Integer.class,
                criado.id()
        );
        assertEquals(List.of(0, 1), ordensAposEdicao);

        Page<MesAnoAtendimentoResponseDTO> recarregadoEdicao =
                atendimentoService.getAtendimentosAgrupadosPorMes(pacienteId, profissionalId, 0, 10);
        List<String> titulosEdicao = recarregadoEdicao.getContent().getFirst().atendimentos().getFirst()
                .relatorio().stream().map(TopicoResponseDTO::titulo).toList();
        assertEquals(List.of("Topico 3 Editado", "Topico 1 Mantido"), titulosEdicao);
    }
}