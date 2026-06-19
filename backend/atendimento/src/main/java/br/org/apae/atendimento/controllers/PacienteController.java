package br.org.apae.atendimento.controllers;

import br.org.apae.atendimento.dtos.response.PacienteDropdownResponseDTO;
import br.org.apae.atendimento.dtos.response.PacienteResponseDTO;
import br.org.apae.atendimento.dtos.response.PaginatedResponseDTO;
import br.org.apae.atendimento.security.UsuarioAutenticado;
import br.org.apae.atendimento.services.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> buscarPorId(
            @PathVariable UUID id,
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado
    ) {
        return ResponseEntity.ok(pacienteService.getPaciente(id, usuarioAutenticado.getId()));
    }

    @GetMapping("/{id}/nome-completo")
    public ResponseEntity<String> obterNomeCompleto(
            @PathVariable UUID id,
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado
    ) {
        String nome = pacienteService.getNomeCompletoPacienteById(id, usuarioAutenticado.getId());
        return ResponseEntity.ok().body(nome);
    }
    
    @GetMapping("/search")
    public ResponseEntity<PaginatedResponseDTO<PacienteResponseDTO>> buscarPacientes(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) String cidade,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado
            ) {

       PaginatedResponseDTO<PacienteResponseDTO> paciente = pacienteService.buscarPaciente(
               usuarioAutenticado.getId(), nome, cpf, cidade, page, limit
       );
       return ResponseEntity.ok(paciente);
   }

    @GetMapping("/todos/search")
    public ResponseEntity<PaginatedResponseDTO<PacienteResponseDTO>> buscarTodosPacientes(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) String cidade,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit
            ) {

       PaginatedResponseDTO<PacienteResponseDTO> paciente = pacienteService.buscarTodosPacientes(
               nome, cpf, cidade, page, limit
       );
       return ResponseEntity.ok(paciente);
   }

    @PostMapping("/{pacienteId}")
    public ResponseEntity<String> adicionarFoto(
            @RequestPart("foto") MultipartFile foto,
            @PathVariable UUID pacienteId,
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado
    ) {
        String urlFoto = pacienteService.adicionarFoto(foto, pacienteId, usuarioAutenticado.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(urlFoto);
    }

    @GetMapping("/dropdown")
    public ResponseEntity<List<PacienteDropdownResponseDTO>> listarParaDropdown(
            @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado
    ) {
        return ResponseEntity.ok(pacienteService.listarParaDropdown(usuarioAutenticado.getId()));
    }
}
