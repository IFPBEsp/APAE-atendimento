package br.org.apae.atendimento.services;

import br.org.apae.atendimento.dtos.request.AgendamentoRequestDTO;
import br.org.apae.atendimento.dtos.response.AgendamentoResponseDTO;
import br.org.apae.atendimento.entities.Agendamento;
import br.org.apae.atendimento.exceptions.invalid.AgendamentoInvalidException;
import br.org.apae.atendimento.mappers.AgendamentoMapper;
import br.org.apae.atendimento.repositories.AgendamentoGeralReadRepository;
import br.org.apae.atendimento.repositories.AgendamentoRepository;
import br.org.apae.atendimento.repositories.AtendimentoRepository;
import br.org.apae.atendimento.repositories.PacienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgendamentoServiceTest {

    @InjectMocks
    private AgendamentoService service;

    @Mock
    private AgendamentoRepository repository;

    @Mock
    private PacienteService pacienteService;

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private AgendamentoMapper agendamentoMapper;

    @Mock
    private AtendimentoRepository atendimentoRepository;

    @Mock
    private AgendamentoGeralReadRepository agendamentoGeralReadRepository;

    private UUID profissionalId;
    private UUID pacienteId;
    private LocalDate data;
    private LocalTime hora;
    private AgendamentoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        profissionalId = UUID.randomUUID();
        pacienteId = UUID.randomUUID();
        data = LocalDate.now().plusDays(1);
        hora = LocalTime.of(10, 0);

        requestDTO = new AgendamentoRequestDTO(
                pacienteId,
                data,
                hora
        );

        lenient().when(pacienteService.existeRelacao(pacienteId, profissionalId)).thenReturn(true);
    }

    @Test
    @DisplayName("Deve lançar erro quando horário já estiver ocupado no Sistema Geral")
    void deveLancarErroQuandoHorarioConflitanteNoSistemaGeral() {
        when(repository.existsByProfissionalIdAndDataHora(profissionalId, LocalDateTime.of(data, hora)))
                .thenReturn(false);
        when(agendamentoGeralReadRepository.existsByProfissionalIdAndDataAndHora(profissionalId, data, hora))
                .thenReturn(true);

        AgendamentoInvalidException exception = assertThrows(
                AgendamentoInvalidException.class,
                () -> service.agendar(requestDTO, profissionalId)
        );

        assertEquals(data + " - " + hora + " ja possui um agendamento", exception.getMessage());
        verify(agendamentoGeralReadRepository).existsByProfissionalIdAndDataAndHora(profissionalId, data, hora);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar erro quando horário já estiver ocupado no Sistema Local")
    void deveLancarErroQuandoHorarioConflitanteNoSistemaLocal() {
        when(repository.existsByProfissionalIdAndDataHora(profissionalId, LocalDateTime.of(data, hora)))
                .thenReturn(true);

        AgendamentoInvalidException exception = assertThrows(
                AgendamentoInvalidException.class,
                () -> service.agendar(requestDTO, profissionalId)
        );

        assertEquals(data + " - " + hora + " ja possui um agendamento", exception.getMessage());
        verify(agendamentoGeralReadRepository, never()).existsByProfissionalIdAndDataAndHora(any(), any(), any());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar erro ao editar quando novo horário já estiver ocupado no Sistema Geral")
    void deveLancarErroAoEditarQuandoHorarioConflitanteNoSistemaGeral() {
        UUID agendamentoId = UUID.randomUUID();
        Agendamento agendamentoExistente = new Agendamento();
        agendamentoExistente.setId(agendamentoId);
        agendamentoExistente.setProfissionalId(profissionalId);
        agendamentoExistente.setPacienteId(pacienteId);
        agendamentoExistente.setDataHora(LocalDateTime.of(data, LocalTime.of(8, 0)));
        agendamentoExistente.setStatus(false);

        when(repository.findByIdAndProfissionalId(agendamentoId, profissionalId))
                .thenReturn(Optional.of(agendamentoExistente));
        when(repository.existsByProfissionalIdAndDataHora(profissionalId, LocalDateTime.of(data, hora)))
                .thenReturn(false);
        when(agendamentoGeralReadRepository.existsByProfissionalIdAndDataAndHora(profissionalId, data, hora))
                .thenReturn(true);

        AgendamentoInvalidException exception = assertThrows(
                AgendamentoInvalidException.class,
                () -> service.editar(agendamentoId, profissionalId, requestDTO)
        );

        assertEquals(data + " - " + hora + " ja possui um agendamento", exception.getMessage());
        verify(agendamentoGeralReadRepository).existsByProfissionalIdAndDataAndHora(profissionalId, data, hora);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve criar agendamento quando não houver conflito de horário local nem no Sistema Geral")
    void deveCriarAgendamentoSemConflitoDeHorario() {
        Agendamento agendamento = new Agendamento();
        AgendamentoResponseDTO responseEsperado = mock(AgendamentoResponseDTO.class);

        when(repository.existsByProfissionalIdAndDataHora(profissionalId, LocalDateTime.of(data, hora)))
                .thenReturn(false);
        when(agendamentoGeralReadRepository.existsByProfissionalIdAndDataAndHora(profissionalId, data, hora))
                .thenReturn(false);
        when(agendamentoMapper.toEntityPadrao(requestDTO)).thenReturn(agendamento);
        when(repository.save(agendamento)).thenReturn(agendamento);
        when(agendamentoMapper.toDTOPadrao(agendamento)).thenReturn(responseEsperado);

        AgendamentoResponseDTO response = service.agendar(requestDTO, profissionalId);

        assertNotNull(response);
        verify(agendamentoGeralReadRepository).existsByProfissionalIdAndDataAndHora(profissionalId, data, hora);
        verify(repository).save(agendamento);
    }
}
