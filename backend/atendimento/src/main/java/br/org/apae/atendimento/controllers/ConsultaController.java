package br.org.apae.atendimento.controllers;

import br.org.apae.atendimento.entities.Consulta;
import br.org.apae.atendimento.entities.Paciente;
import br.org.apae.atendimento.entities.ProfissionalSaude;
import br.org.apae.atendimento.services.ConsultaService;
import br.org.apae.atendimento.services.PacienteService;
import br.org.apae.atendimento.services.ProfissionalSaudeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consultas")
public class ConsultaController {

    @Autowired
    private ConsultaService consultaService;

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private ProfissionalSaudeService profissionalSaudeService;

    @PostMapping
    public ResponseEntity<Consulta> criarConsulta(
            @RequestBody Consulta consulta,
            @RequestParam Long pacienteId,
            @RequestParam Long profissionalId
    ) {
        Consulta novaConsulta = consultaService.addConsulta(consulta, pacienteId, profissionalId);
        return ResponseEntity.status(HttpStatus.created).body(novaConsulta);
    }

    @GetMapping("/paciente/{id}")
    public ResponseEntity<List<Consulta>> listarConsultasDoPaciente(@PathVariable Long id) {
        List<Consulta> consultas = consultaService.getConsultasDoPaciente(id);
        return ResponseEntity.ok(consultas);
    }
}
