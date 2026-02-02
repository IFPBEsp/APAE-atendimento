package br.org.apae.atendimento.controllers;

import br.org.apae.atendimento.dtos.request.AtendimentoRequestDTO;
import br.org.apae.atendimento.dtos.response.AtendimentoResponseDTO;
import br.org.apae.atendimento.dtos.response.MesAnoAtendimentoResponseDTO;
import br.org.apae.atendimento.services.AtendimentoService;
import br.org.apae.atendimento.services.PacienteService;
import br.org.apae.atendimento.services.ProfissionalSaudeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.org.apae.atendimento.security.UsuarioAutenticado;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

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
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado) {
        AtendimentoResponseDTO novoAtendimento = atendimentoService.addAtendimento(atendimento,
                usuarioAutenticado.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(novoAtendimento);
    }

    @GetMapping("/{pacienteId}")
    public ResponseEntity<List<MesAnoAtendimentoResponseDTO>> listarAtendimentosDoPaciente(
            @PathVariable UUID pacienteId,
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado) {
        List<MesAnoAtendimentoResponseDTO> atendimentos = atendimentoService.getAtendimentosAgrupadosPorMes(
                pacienteId, usuarioAutenticado.getId());
        return ResponseEntity.ok().body(atendimentos);
    }

    @DeleteMapping("/{pacienteId}/{atendimentoId}")
    public ResponseEntity<String> deletar(@PathVariable UUID pacienteId,
            @PathVariable UUID atendimentoId,
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado) {
        atendimentoService.deletar(usuarioAutenticado.getId(), pacienteId, atendimentoId);
        return ResponseEntity.ok().body("Atendimento excluído");
    }

    @PutMapping("/{atendimentoId}")
    public ResponseEntity<AtendimentoResponseDTO> editarTopicos(@RequestBody AtendimentoRequestDTO updateDTO,
            @PathVariable UUID atendimentoId,
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado) {
        AtendimentoResponseDTO atendimentoAtualizado = atendimentoService.editar(updateDTO, atendimentoId,
                usuarioAutenticado.getId());
        return ResponseEntity.ok().body(atendimentoAtualizado);
    }
}
