package br.org.apae.atendimento.repositories;

import br.org.apae.atendimento.entities.Agendamento;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;INIT=CREATE SCHEMA IF NOT EXISTS atendimento",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.flyway.enabled=false",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class AgendamentoRepositoryTest {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        entityManager.getEntityManager()
                .createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE")
                .executeUpdate();
    }

    private Agendamento criarEPersistirAgendamento(UUID profissionalId, UUID pacienteId, LocalDateTime dataHora) {
        Agendamento agendamento = new Agendamento();
        agendamento.setProfissionalId(profissionalId);
        agendamento.setPacienteId(pacienteId);
        agendamento.setDataHora(dataHora);
        return entityManager.persistAndFlush(agendamento);
    }

    @Test
    @DisplayName("Deve retornar agendamentos por profissional ordenados por data decrescente")
    void findByProfissionalIdOrderByDataHoraDesc_DeveRetornarOrdenado() {
        UUID profissionalId = UUID.randomUUID();
        criarEPersistirAgendamento(profissionalId, UUID.randomUUID(), LocalDateTime.now().minusDays(2));
        criarEPersistirAgendamento(profissionalId, UUID.randomUUID(), LocalDateTime.now());

        List<Agendamento> resultado = agendamentoRepository.findByProfissionalIdOrderByDataHoraDesc(profissionalId);

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getDataHora()).isAfter(resultado.get(1).getDataHora());
    }

    @Test
    @DisplayName("Deve retornar agendamentos no intervalo de datas ordenados decrescentemente")
    void findByProfissionalIdAndDataHoraBetweenOrderByDataHoraDesc_DeveFiltrarPorIntervalo() {
        UUID profissionalId = UUID.randomUUID();
        UUID paciente20 = UUID.randomUUID();
        UUID paciente30 = UUID.randomUUID();
        LocalDateTime hoje = LocalDateTime.now();
        
        criarEPersistirAgendamento(profissionalId, UUID.randomUUID(), hoje.minusDays(5));
        criarEPersistirAgendamento(profissionalId, paciente20, hoje);
        criarEPersistirAgendamento(profissionalId, paciente30, hoje.minusDays(1));
        criarEPersistirAgendamento(profissionalId, UUID.randomUUID(), hoje.plusDays(5));

        List<Agendamento> resultado = agendamentoRepository
                .findByProfissionalIdAndDataHoraBetweenOrderByDataHoraDesc(profissionalId, hoje.minusDays(2), hoje.plusDays(1));

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getPacienteId()).isEqualTo(paciente20);
        assertThat(resultado.get(1).getPacienteId()).isEqualTo(paciente30);
    }

    @Test
    @DisplayName("Deve encontrar agendamento exato por dataInicio (inclusiva) e dataFim (exclusiva)")
    void findByDataHoraAndProfissionalIdAndPacienteId_DeveRespeitarLogicaDeIntervaloCustomizada() {
        UUID profissionalId = UUID.randomUUID();
        UUID pacienteId = UUID.randomUUID();
        LocalDateTime inicio = LocalDateTime.of(2026, 11, 6, 10, 0);
        LocalDateTime fim = LocalDateTime.of(2026, 11, 6, 11, 0);

        Agendamento salvo = criarEPersistirAgendamento(profissionalId, pacienteId, inicio.plusMinutes(15));

        Optional<Agendamento> resultado = agendamentoRepository
                .findByDataHoraAndProfissionalIdAndPacienteId(inicio, fim, profissionalId, pacienteId);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(salvo.getId());
    }

    @Test
    @DisplayName("Deve retornar true se existir agendamento, false caso contrário")
    void existsByProfissionalIdAndDataHora_DeveRetornarTrueOuFalse() {
        UUID profissionalId = UUID.randomUUID();
        LocalDateTime dataHora = LocalDateTime.of(2026, 11, 6, 14, 30, 0);
        criarEPersistirAgendamento(profissionalId, UUID.randomUUID(), dataHora);

        boolean existe = agendamentoRepository.existsByProfissionalIdAndDataHora(profissionalId, dataHora);
        assertThat(existe).isTrue();

        boolean existeFalso = agendamentoRepository.existsByProfissionalIdAndDataHora(profissionalId, dataHora.plusHours(1));
        assertThat(existeFalso).isFalse();
    }

    @Test
    @DisplayName("Deve buscar agendamento exato por ID, Profissional ID e Paciente ID")
    void findByIdAndProfissionalIdAndPacienteId_DeveRetornarCorretamente() {
        UUID profissionalId = UUID.randomUUID();
        UUID pacienteId = UUID.randomUUID();
        Agendamento salvo = criarEPersistirAgendamento(profissionalId, pacienteId, LocalDateTime.now());

        Optional<Agendamento> resultado = agendamentoRepository
                .findByIdAndProfissionalIdAndPacienteId(salvo.getId(), profissionalId, pacienteId);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(salvo.getId());
    }

    @Test
    @DisplayName("Deve buscar agendamento exato por ID e Profissional ID")
    void findByIdAndProfissionalId_DeveRetornarCorretamente() {
        UUID profissionalId = UUID.randomUUID();
        Agendamento salvo = criarEPersistirAgendamento(profissionalId, UUID.randomUUID(), LocalDateTime.now());

        Optional<Agendamento> resultado = agendamentoRepository
                .findByIdAndProfissionalId(salvo.getId(), profissionalId);

        assertThat(resultado).isPresent();
    }
}