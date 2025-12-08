package br.org.apae.atendimento.controllers;

import br.org.apae.atendimento.dtos.request.AgendamentoRequestDTO;
import br.org.apae.atendimento.dtos.response.AgendamentoResponseDTO;
import br.org.apae.atendimento.dtos.response.DiaAgendamentoResponseDTO;
import br.org.apae.atendimento.services.AgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/agendamento")
public class AgendamentoController {
    @Autowired
    private AgendamentoService service;

    @PostMapping()
    public ResponseEntity<AgendamentoResponseDTO> agendarPaciente(@RequestBody AgendamentoRequestDTO agendamentoRequest){
        AgendamentoResponseDTO agendamento = service.agendar(agendamentoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(agendamento);
    }

    @GetMapping("/{profissionalId}")
    public ResponseEntity<List<DiaAgendamentoResponseDTO>> listarAgendamentoAgrupadoPorDia(
            @PathVariable UUID profissionalId
    ){
        List<DiaAgendamentoResponseDTO> agendamentos = service.listarAgrupadoPorDia(profissionalId);
        return ResponseEntity.ok().body(agendamentos);
    }

    @DeleteMapping("/{profissionalId}/{pacienteId}/{agendamentoId}")
    public ResponseEntity<String> deletarAgendamento(@PathVariable UUID profissionalId,
                                                     @PathVariable UUID pacienteId,
                                                     @PathVariable UUID agendamentoId){
        service.deletar(profissionalId, pacienteId, agendamentoId);
        return ResponseEntity.ok().body("Agendamento excluído");
    }
}
