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
}