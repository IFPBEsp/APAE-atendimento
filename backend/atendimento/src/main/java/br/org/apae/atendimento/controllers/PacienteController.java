package br.org.apae.atendimento.controllers;

import br.org.apae.atendimento.dtos.response.PacienteResponseDTO;
import br.org.apae.atendimento.services.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<PacienteResponseDTO> buscarPorId(@PathVariable UUID id) {
        PacienteResponseDTO paciente = pacienteService.getPaciente(id);
        return ResponseEntity.ok(paciente);
    }

    @GetMapping("/{id}/nome-completo")
    public ResponseEntity<String> obterPrimeiroNome(@PathVariable UUID id) {
        String nome = pacienteService.getNomeCompletoPacienteById(id);
        return ResponseEntity.ok().body(nome);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<PacienteResponseDTO>> buscarPacientes(
           @RequestParam(required = false) String nome,
           @RequestParam(required = false) String cpf,
           @RequestParam(required = false) String cidade
    ) {

       List<PacienteResponseDTO> paciente = pacienteService.buscarPaciente(nome, cpf, cidade);
       return ResponseEntity.ok(paciente);
   }

   @PostMapping("/{pacienteId}")
   public ResponseEntity<String> adicionarFoto(@RequestPart("foto") MultipartFile foto, @PathVariable UUID pacienteId){
        String urlFoto = pacienteService.adicionarFoto(foto, pacienteId);

        return  ResponseEntity.status(HttpStatus.CREATED).body(urlFoto);
   }
}
