package br.org.apae.atendimento.services;

import br.org.apae.atendimento.dtos.request.AtendimentoRequestDTO;
import br.org.apae.atendimento.dtos.request.TopicoRequestDTO;
import br.org.apae.atendimento.dtos.response.AtendimentoResponseDTO;
import br.org.apae.atendimento.dtos.response.MesAnoAtendimentoResponseDTO;
import br.org.apae.atendimento.entities.Agendamento;
import br.org.apae.atendimento.entities.Atendimento;
import br.org.apae.atendimento.entities.ProfissionalSaude;
import br.org.apae.atendimento.entities.Topico;
import br.org.apae.atendimento.exceptions.invalid.AtendimentoInvalidException;
import br.org.apae.atendimento.exceptions.invalid.RelacaoInvalidException;
import br.org.apae.atendimento.exceptions.notfound.AgendamentoNotFoundException;
import br.org.apae.atendimento.exceptions.notfound.AtendimentoNotFoundException;
import br.org.apae.atendimento.mappers.AtendimentoMapper;
import br.org.apae.atendimento.repositories.AtendimentoRepository;
import br.org.apae.atendimento.repositories.ProfissionalSaudeRepository;
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
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtendimentoServiceTest {

    @InjectMocks
    private AtendimentoService service;

    @Mock
    private AtendimentoRepository repository;

    @Mock
    private ProfissionalSaudeRepository profissionalRepository;

    @Mock
    private AgendamentoService agendamentoService;

    @Mock
    private AtendimentoMapper atendimentoMapper;

    @Mock
    private PacienteService pacienteService;

    private UUID profissionalId;
    private UUID pacienteId;
    private AtendimentoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        profissionalId = UUID.randomUUID();
        pacienteId = UUID.randomUUID();

        requestDTO = new AtendimentoRequestDTO(
                pacienteId,
                List.of(new TopicoRequestDTO("Titulo", "Descricao")),
                LocalDate.now(),
                LocalTime.of(10, 0)
        );
    }

    @Test
    @DisplayName("Deve criar atendimento com numeracao global incrementada")
    void deveCriarAtendimentoComNumeracaoGlobal() {
        when(pacienteService.existeRelacao(pacienteId, profissionalId)).thenReturn(true);
        when(repository.existsByProfissionalIdAndDataAtendimento(any(), any())).thenReturn(false);
        when(repository.findMaxNumeracaoByMesAndAno(LocalDate.now().getMonthValue(), LocalDate.now().getYear(), profissionalId)).thenReturn(1L);
        when(atendimentoMapper.toEntityPadrao(requestDTO)).thenReturn(new Atendimento());
        when(profissionalRepository.getReferenceById(profissionalId)).thenReturn(new ProfissionalSaude());

        Atendimento entity = new Atendimento();
        when(repository.save(any())).thenReturn(entity);
        when(atendimentoMapper.toDTOPadrao(entity)).thenReturn(mock(AtendimentoResponseDTO.class));

        when(agendamentoService.buscarAgendamentoPorDataEPaciente(any(), any()))
                .thenReturn(new Agendamento());

        AtendimentoResponseDTO response = service.addAtendimento(requestDTO, profissionalId);

        assertNotNull(response);
        verify(repository).findMaxNumeracaoByMesAndAno(LocalDate.now().getMonthValue(), LocalDate.now().getYear(), profissionalId);
        verify(repository).save(any());
    }

    @Test
    @DisplayName("Deve lançar erro quando profissional não possui vínculo com paciente")
    void deveLancarErroQuandoSemRelacao() {
        when(pacienteService.existeRelacao(pacienteId, profissionalId)).thenReturn(false);

        assertThrows(RelacaoInvalidException.class,
                () -> service.addAtendimento(requestDTO, profissionalId));

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar erro quando já existe atendimento no horário")
    void deveLancarErroQuandoHorarioConflitante() {
        when(pacienteService.existeRelacao(pacienteId, profissionalId)).thenReturn(true);
        when(repository.existsByProfissionalIdAndDataAtendimento(any(), any())).thenReturn(true);

        assertThrows(AtendimentoInvalidException.class,
                () -> service.addAtendimento(requestDTO, profissionalId));

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar erro quando relatorio estiver vazio")
    void deveLancarErroQuandoRelatorioVazio() {
        AtendimentoRequestDTO dtoSemRelatorio = new AtendimentoRequestDTO(
                pacienteId,
                List.of(),
                LocalDate.now(),
                LocalTime.of(10, 0)
        );

        when(pacienteService.existeRelacao(pacienteId, profissionalId)).thenReturn(true);
        when(repository.existsByProfissionalIdAndDataAtendimento(any(), any())).thenReturn(false);
        when(atendimentoMapper.toEntityPadrao(dtoSemRelatorio)).thenReturn(new Atendimento());

        assertThrows(AtendimentoInvalidException.class,
                () -> service.addAtendimento(dtoSemRelatorio, profissionalId));

        verify(atendimentoMapper).toEntityPadrao(dtoSemRelatorio);
    }

    @Test
    @DisplayName("Deve editar atendimento e substituir relatorio")
    void deveEditarAtendimento() {
        when(pacienteService.existeRelacao(pacienteId, profissionalId)).thenReturn(true);

        Atendimento atendimento = new Atendimento();
        atendimento.setDataAtendimento(LocalDateTime.now());
        atendimento.getRelatorio().add(new Topico());

        when(repository.findByIdComRelatorio(any())).thenReturn(Optional.of(atendimento));
        when(repository.save(atendimento)).thenReturn(atendimento);
        when(atendimentoMapper.toDTOPadrao(atendimento)).thenReturn(mock(AtendimentoResponseDTO.class));

        AtendimentoResponseDTO response = service.editar(requestDTO, UUID.randomUUID(), profissionalId);

        assertNotNull(response);
        assertFalse(atendimento.getRelatorio().isEmpty());
    }

    @Test
    @DisplayName("Deve lançar erro ao editar atendimento inexistente")
    void deveLancarErroQuandoAtendimentoNaoEncontrado() {
        when(pacienteService.existeRelacao(pacienteId, profissionalId)).thenReturn(true);
        when(repository.findByIdComRelatorio(any())).thenReturn(Optional.empty());

        assertThrows(AtendimentoNotFoundException.class,
                () -> service.editar(requestDTO, UUID.randomUUID(), profissionalId));
    }

    @Test
    @DisplayName("Deve agrupar atendimentos por mês")
    void deveAgruparAtendimentosPorMes() {
        Atendimento a1 = new Atendimento();
        a1.setDataAtendimento(LocalDateTime.of(2026, 5, 10, 10, 0));
        Atendimento a2 = new Atendimento();
        a2.setDataAtendimento(LocalDateTime.of(2026, 6, 5, 11, 0));

        when(repository.findByPacienteIdAndProfissionalIdComRelatorio(pacienteId, profissionalId))
                .thenReturn(List.of(a1, a2));
        when(atendimentoMapper.toDTOPadrao(any())).thenReturn(mock(AtendimentoResponseDTO.class));

        List<MesAnoAtendimentoResponseDTO> result =
                service.getAtendimentosAgrupadosPorMes(pacienteId, profissionalId);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(r -> r.mesAno().equals(YearMonth.of(2026, 5))));
        assertTrue(result.stream().anyMatch(r -> r.mesAno().equals(YearMonth.of(2026, 6))));
    }

    @Test
    @DisplayName("Deve ignorar AgendamentoNotFoundException ao criar atendimento")
    void deveIgnorarAgendamentoNotFoundException() {
        when(pacienteService.existeRelacao(pacienteId, profissionalId)).thenReturn(true);
        when(repository.existsByProfissionalIdAndDataAtendimento(any(), any())).thenReturn(false);
        when(repository.findMaxNumeracaoByMesAndAno(LocalDate.now().getMonthValue(), LocalDate.now().getYear(), profissionalId)).thenReturn(0L);
        when(atendimentoMapper.toEntityPadrao(requestDTO)).thenReturn(new Atendimento());
        when(profissionalRepository.getReferenceById(profissionalId)).thenReturn(new ProfissionalSaude());

        Atendimento entity = new Atendimento();
        when(repository.save(any())).thenReturn(entity);
        when(atendimentoMapper.toDTOPadrao(entity)).thenReturn(mock(AtendimentoResponseDTO.class));

        doThrow(new AgendamentoNotFoundException("nao encontrado"))
                .when(agendamentoService)
                .buscarAgendamentoPorDataEPaciente(any(), any());

        assertDoesNotThrow(() -> service.addAtendimento(requestDTO, profissionalId));
    }

    @Test
    @DisplayName("Deve setar numeracao correta ao salvar atendimento")
    void deveSetarNumeracaoCorretaAoSalvar() {
        when(pacienteService.existeRelacao(pacienteId, profissionalId)).thenReturn(true);
        when(repository.existsByProfissionalIdAndDataAtendimento(any(), any())).thenReturn(false);
        when(repository.findMaxNumeracaoByMesAndAno(LocalDate.now().getMonthValue(), LocalDate.now().getYear(), profissionalId)).thenReturn(5L);

        Atendimento entity = new Atendimento();
        when(atendimentoMapper.toEntityPadrao(requestDTO)).thenReturn(entity);
        when(profissionalRepository.getReferenceById(profissionalId)).thenReturn(new ProfissionalSaude());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(atendimentoMapper.toDTOPadrao(any())).thenReturn(mock(AtendimentoResponseDTO.class));

        when(agendamentoService.buscarAgendamentoPorDataEPaciente(any(), any()))
                .thenReturn(new Agendamento());

        service.addAtendimento(requestDTO, profissionalId);

        assertEquals(6L, entity.getNumeracao());
    }
}