package br.org.apae.atendimento.repositories;

import br.org.apae.atendimento.entities.Agendamento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AgendamentoRepositoryTest {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Agendamento criarEPersistirAgendamento(Long profissionalId, Long pacienteId, LocalDateTime dataHora) {
        Agendamento agendamento = new Agendamento();
        agendamento.setProfissionalId(profissionalId);
        agendamento.setPacienteId(pacienteId);
        agendamento.setDataHora(dataHora);
        return entityManager.persistAndFlush(agendamento);
    }

    @Test
    @DisplayName("Deve retornar agendamentos por profissional ordenados por data decrescente")
    void findByProfissionalIdOrderByDataHoraDesc_DeveRetornarOrdenado() {
        Long profissionalId = 1L;
        criarEPersistirAgendamento(profissionalId, 10L, LocalDateTime.now().minusDays(2));
        criarEPersistirAgendamento(profissionalId, 20L, LocalDateTime.now());

        List<Agendamento> resultado = agendamentoRepository.findByProfissionalIdOrderByDataHoraDesc(profissionalId);

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getDataHora()).isAfter(resultado.get(1).getDataHora());
    }

    @Test
    @DisplayName("Deve retornar agendamentos no intervalo de datas ordenados decrescentemente")
    void findByProfissionalIdAndDataHoraBetweenOrderByDataHoraDesc_DeveFiltrarPorIntervalo() {
        Long profissionalId = 2L;
        LocalDateTime hoje = LocalDateTime.now();
        
        criarEPersistirAgendamento(profissionalId, 10L, hoje.minusDays(5));
        criarEPersistirAgendamento(profissionalId, 20L, hoje);
        criarEPersistirAgendamento(profissionalId, 30L, hoje.minusDays(1));
        criarEPersistirAgendamento(profissionalId, 40L, hoje.plusDays(5));

        List<Agendamento> resultado = agendamentoRepository
                .findByProfissionalIdAndDataHoraBetweenOrderByDataHoraDesc(profissionalId, hoje.minusDays(2), hoje.plusDays(1));

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getPacienteId()).isEqualTo(20L);
        assertThat(resultado.get(1).getPacienteId()).isEqualTo(30L);
    }

    @Test
    @DisplayName("Deve encontrar por dataInicio (inclusiva), dataFim (exclusiva), profissional e paciente")
    void findByDataHoraAndProfissionalIdAndPacienteId_DeveRespeitarLogicaDeIntervaloCustomizada() {
        Long profissionalId = 3L;
        Long pacienteId = 100L;
        LocalDateTime inicio = LocalDateTime.of(2026, 11, 6, 10, 0);
        LocalDateTime fim = LocalDateTime.of(2026, 11, 6, 11, 0);

        criarEPersistirAgendamento(profissionalId, pacienteId, inicio);
        criarEPersistirAgendamento(profissionalId, pacienteId, inicio.plusMinutes(30));
        criarEPersistirAgendamento(profissionalId, pacienteId, fim);

        List<Agendamento> resultado = agendamentoRepository
                .findByDataHoraAndProfissionalIdAndPacienteId(inicio, fim, profissionalId, pacienteId);

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(Agendamento::getDataHora)
                .containsExactlyInAnyOrder(inicio, inicio.plusMinutes(30));
    }

    @Test
    @DisplayName("Deve retornar true se existir agendamento, false caso contrário")
    void existsByProfissionalIdAndDataHora_DeveRetornarTrueOuFalse() {
        Long profissionalId = 4L;
        LocalDateTime dataHora = LocalDateTime.now();
        criarEPersistirAgendamento(profissionalId, 1L, dataHora);

        boolean existe = agendamentoRepository.existsByProfissionalIdAndDataHora(profissionalId, dataHora);
        assertThat(existe).isTrue();

        boolean existeFalso = agendamentoRepository.existsByProfissionalIdAndDataHora(profissionalId, dataHora.plusHours(1));
        assertThat(existeFalso).isFalse();
    }

    @Test
    @DisplayName("Deve buscar agendamento exato por ID, Profissional ID e Paciente ID")
    void findByIdAndProfissionalIdAndPacienteId_DeveRetornarCorretamente() {
        Long profissionalId = 5L;
        Long pacienteId = 200L;
        Agendamento salvo = criarEPersistirAgendamento(profissionalId, pacienteId, LocalDateTime.now());

        Optional<Agendamento> resultado = agendamentoRepository
                .findByIdAndProfissionalIdAndPacienteId(salvo.getId(), profissionalId, pacienteId);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(salvo.getId());
    }

    @Test
    @DisplayName("Deve buscar agendamento exato por ID e Profissional ID")
    void findByIdAndProfissionalId_DeveRetornarCorretamente() {
        Long profissionalId = 6L;
        Agendamento salvo = criarEPersistirAgendamento(profissionalId, 300L, LocalDateTime.now());

        Optional<Agendamento> resultado = agendamentoRepository
                .findByIdAndProfissionalId(salvo.getId(), profissionalId);

        assertThat(resultado).isPresent();
    }
}