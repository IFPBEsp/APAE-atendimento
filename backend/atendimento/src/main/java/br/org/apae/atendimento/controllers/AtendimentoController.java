package br.org.apae.atendimento.controllers;

import br.org.apae.atendimento.dtos.request.AtendimentoRequestDTO;
import br.org.apae.atendimento.dtos.response.AtendimentoResponseDTO;
import br.org.apae.atendimento.dtos.response.MesAnoAtendimentoResponseDTO;
import br.org.apae.atendimento.services.AtendimentoService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.org.apae.atendimento.security.UsuarioAutenticado;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.UUID;

@RestController
@RequestMapping("/atendimentos")
public class AtendimentoController implements AtendimentoControllerDocs {

    @Autowired
    private AtendimentoService atendimentoService;

    @Override
    @PostMapping
    public ResponseEntity<AtendimentoResponseDTO> criarAtendimento(
            @Valid @RequestBody AtendimentoRequestDTO atendimento,
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado) {
        AtendimentoResponseDTO novoAtendimento = atendimentoService.addAtendimento(atendimento,
                usuarioAutenticado.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(novoAtendimento);
    }

    @Override
    @GetMapping("/{pacienteId}")
    public ResponseEntity<Page<MesAnoAtendimentoResponseDTO>> listarAtendimentosDoPaciente(
            @PathVariable UUID pacienteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado) {
        Page<MesAnoAtendimentoResponseDTO> atendimentos = atendimentoService.getAtendimentosAgrupadosPorMes(
                pacienteId, usuarioAutenticado.getId(), PageRequest.of(page, size));
        return ResponseEntity.ok().body(atendimentos);
    }

    @Override
    @DeleteMapping("/{pacienteId}/{atendimentoId}")
    public ResponseEntity<String> deletar(@PathVariable UUID pacienteId,
            @PathVariable UUID atendimentoId,
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado) {
        atendimentoService.deletar(usuarioAutenticado.getId(), pacienteId, atendimentoId);
        return ResponseEntity.ok().body("Atendimento excluído");
    }

    @Override
    @PutMapping("/{atendimentoId}")
    public ResponseEntity<AtendimentoResponseDTO> editarTopicos(@Valid @RequestBody AtendimentoRequestDTO updateDTO,
            @PathVariable UUID atendimentoId,
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado) {
        AtendimentoResponseDTO atendimentoAtualizado = atendimentoService.editar(updateDTO, atendimentoId,
                usuarioAutenticado.getId());
        return ResponseEntity.ok().body(atendimentoAtualizado);
    }

    @Override
    @PatchMapping("/{atendimentoId}/concluir")
    public ResponseEntity<String> concluirAtendimento(
            @PathVariable UUID atendimentoId,
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado
    ) {
        atendimentoService.concluirAtendimento(usuarioAutenticado.getId(), atendimentoId);
        return ResponseEntity.ok().body("Atendimento concluído com sucesso");
    }
}
