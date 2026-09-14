package br.org.apae.atendimento.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.org.apae.atendimento.dtos.request.AgendamentoRequestDTO;
import br.org.apae.atendimento.dtos.response.AgendamentoResponseDTO;
import br.org.apae.atendimento.dtos.response.DiaAgendamentoResponseDTO;
import br.org.apae.atendimento.security.UsuarioAutenticado;
import br.org.apae.atendimento.services.AgendamentoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/agendamento")
public class AgendamentoController {
    @Autowired
    private AgendamentoService service;

    @PostMapping()
    public ResponseEntity<AgendamentoResponseDTO> agendarPaciente(
            @Valid @RequestBody AgendamentoRequestDTO agendamentoRequest,
            @AuthenticationPrincipal UsuarioAutenticado usuarioAtenticado
    ){
        AgendamentoResponseDTO agendamento = service.agendar(agendamentoRequest, usuarioAtenticado.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(agendamento);
    }

    @PutMapping("/{agendamentoId}")
    public ResponseEntity<AgendamentoResponseDTO> editarAgendamento(
            @PathVariable UUID agendamentoId,
            @Valid @RequestBody AgendamentoRequestDTO agendamentoRequest,
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado
    ) {
        AgendamentoResponseDTO response = service.editar(agendamentoId, usuarioAutenticado.getId(), agendamentoRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping()
    public ResponseEntity<Page<DiaAgendamentoResponseDTO>> listarAgendamentoAgrupadoPorDia(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UsuarioAutenticado usuarioAtenticado
            ){
        Page<DiaAgendamentoResponseDTO> agendamentos = service.listarAgrupadoPorDia(
                usuarioAtenticado.getId(), data, page, size);
        return ResponseEntity.ok().body(agendamentos);
    }

    @DeleteMapping("/{pacienteId}/{agendamentoId}")
    public ResponseEntity<String> deletarAgendamento(@PathVariable UUID pacienteId,
                                                     @PathVariable UUID agendamentoId,
                                                     @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado){
        service.deletar(usuarioAutenticado.getId(), pacienteId, agendamentoId);
        return ResponseEntity.ok().body("Agendamento excluído");
    }

    @PatchMapping("/{pacienteId}/{agendamentoId}/concluir")
    public ResponseEntity<String> concluirAgendamento(@PathVariable UUID pacienteId,
                                                     @PathVariable UUID agendamentoId,
                                                     @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado) {
        service.concluir(usuarioAutenticado.getId(), pacienteId, agendamentoId);
        return ResponseEntity.ok().body("Agendamento concluído");
    }
}