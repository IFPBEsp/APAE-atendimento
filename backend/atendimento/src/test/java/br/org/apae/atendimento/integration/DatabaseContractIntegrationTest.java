package br.org.apae.atendimento.integration;

import br.org.apae.atendimento.entities.Atendimento;
import br.org.apae.atendimento.entities.Paciente;
import br.org.apae.atendimento.entities.ProfissionalSaude;
import br.org.apae.atendimento.repositories.AtendimentoRepository;
import br.org.apae.atendimento.repositories.PacienteRepository;
import br.org.apae.atendimento.repositories.ProfissionalSaudeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class DatabaseContractIntegrationTest extends AbstractIntegrationTest {

    private static final UUID PROFISSIONAL_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private static final UUID PACIENTE_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private ProfissionalSaudeRepository profissionalSaudeRepository;

    @Autowired
    private AtendimentoRepository atendimentoRepository;

    @Test
    @DisplayName("Deve ler views e escrever em tabela propria do schema atendimento")
    void deveValidarContratoEntreApaeGeralEAtendimento() {
        Paciente paciente = pacienteRepository.findByIdAndProfissionalId(PACIENTE_ID, PROFISSIONAL_ID)
                .orElseThrow(() -> new AssertionError("vw_pacientes nao retornou o paciente de teste"));

        assertEquals(PACIENTE_ID, paciente.getId());
        assertEquals("Joao Pedro Silva", paciente.getNomeCompleto());
        assertFalse(paciente.getResponsaveis().isEmpty());
        assertFalse(paciente.getTranstornos().isEmpty());

        ProfissionalSaude profissional = profissionalSaudeRepository.findById(PROFISSIONAL_ID)
                .orElseThrow(() -> new AssertionError("vw_profissional_saude nao retornou o profissional de teste"));

        assertEquals(PROFISSIONAL_ID, profissional.getId());
        assertEquals("Dr. Luiz Artur", profissional.getNomeCompleto());
        assertEquals("CRP-123", profissional.getRegistroProfissional());
        assertTrue(profissional.getAtivo());

        Atendimento atendimento = new Atendimento();
        atendimento.setNumeracao("900");
        atendimento.setPacienteId(PACIENTE_ID);
        atendimento.setProfissionalId(PROFISSIONAL_ID);
        atendimento.setDataAtendimento(LocalDateTime.of(2026, 6, 3, 9, 30));
        atendimento.setStatus(false);

        Atendimento salvo = atendimentoRepository.saveAndFlush(atendimento);

        assertNotNull(salvo.getId());
        assertTrue(atendimentoRepository.existsById(salvo.getId()));
    }
}
