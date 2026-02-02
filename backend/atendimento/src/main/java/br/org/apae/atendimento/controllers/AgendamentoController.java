package br.org.apae.atendimento.controllers;

import br.org.apae.atendimento.dtos.request.AgendamentoRequestDTO;
import br.org.apae.atendimento.dtos.response.AgendamentoResponseDTO;
import br.org.apae.atendimento.dtos.response.DiaAgendamentoResponseDTO;
import br.org.apae.atendimento.security.UsuarioAutenticado;
import br.org.apae.atendimento.services.AgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/agendamento")
public class AgendamentoController {
    @Autowired
    private AgendamentoService service;

    @PostMapping()
    public ResponseEntity<AgendamentoResponseDTO> agendarPaciente(
            @RequestBody AgendamentoRequestDTO agendamentoRequest,
            @AuthenticationPrincipal UsuarioAutenticado usuarioAtenticado
    ){
        AgendamentoResponseDTO agendamento = service.agendar(agendamentoRequest, usuarioAtenticado.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(agendamento);
    }

    @GetMapping()
    public ResponseEntity<List<DiaAgendamentoResponseDTO>> listarAgendamentoAgrupadoPorDia(
            @AuthenticationPrincipal UsuarioAutenticado usuarioAtenticado
            ){
        List<DiaAgendamentoResponseDTO> agendamentos = service.listarAgrupadoPorDia(usuarioAtenticado.getId());
        return ResponseEntity.ok().body(agendamentos);
    }

    @DeleteMapping("/{pacienteId}/{agendamentoId}")
    public ResponseEntity<String> deletarAgendamento(@PathVariable UUID pacienteId,
                                                     @PathVariable UUID agendamentoId,
                                                     @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado){
        service.deletar(usuarioAutenticado.getId(), pacienteId, agendamentoId);
        return ResponseEntity.ok().body("Agendamento excluído");
    }
}
