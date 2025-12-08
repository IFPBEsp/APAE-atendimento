package br.org.apae.atendimento.controllers;

import br.org.apae.atendimento.dtos.request.AgendamentoRequestDTO;
import br.org.apae.atendimento.dtos.request.AtendimentoRequestDTO;
import br.org.apae.atendimento.dtos.response.AgendamentoResponseDTO;
import br.org.apae.atendimento.dtos.response.AtendimentoResponseDTO;
import br.org.apae.atendimento.dtos.response.DiaAgendamentoResponseDTO;
import br.org.apae.atendimento.dtos.response.MesAnoAtendimentoResponseDTO;
import br.org.apae.atendimento.services.AtendimentoService;
import br.org.apae.atendimento.services.PacienteService;
import br.org.apae.atendimento.services.ProfissionalSaudeService;

import jakarta.websocket.server.PathParam;
import org.simpleframework.xml.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/atendimentos")
public class AtendimentoController {

    @Autowired
    private AtendimentoService atendimentoService;

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private ProfissionalSaudeService profissionalSaudeService;

    @PostMapping
    public ResponseEntity<AtendimentoResponseDTO> criarAtendimento(
            @RequestBody AtendimentoRequestDTO atendimento,
            @RequestParam(required = false) UUID agendamentoId
    ) {
        AtendimentoResponseDTO novoAtendimento = atendimentoService.addAtendimento(atendimento, agendamentoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoAtendimento);
    }

    @GetMapping("/{profissionalId}/{pacienteId}")
    public ResponseEntity<List<MesAnoAtendimentoResponseDTO>> listarAtendimentosDoPaciente(
            @PathVariable UUID pacienteId,
            @PathVariable UUID profissionalId
    ) {
        List<MesAnoAtendimentoResponseDTO> atendimentos = atendimentoService.getAtendimentosAgrupadosPorMes(
                pacienteId, profissionalId);
        return ResponseEntity.ok().body(atendimentos);
    }


    @DeleteMapping("/{profissionalId}/{pacienteId}/{atendimentoId}")
    public ResponseEntity<String> deletar(@PathVariable UUID profissionalId,
                                                     @PathVariable UUID pacienteId,
                                                     @PathVariable UUID atendimentoId){
        atendimentoService.deletar(profissionalId, pacienteId, atendimentoId);
        return ResponseEntity.ok().body("Atendimento excluído");
    }
}
