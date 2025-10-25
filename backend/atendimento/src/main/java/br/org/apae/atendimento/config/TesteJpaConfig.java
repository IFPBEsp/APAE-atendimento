package br.org.apae.atendimento.config;

import br.org.apae.atendimento.entities.Consulta;
import br.org.apae.atendimento.entities.Paciente;
import br.org.apae.atendimento.entities.ProfissionalSaude;
import br.org.apae.atendimento.repositories.ConsultaRepository;
import br.org.apae.atendimento.repositories.PacienteRepository;
import br.org.apae.atendimento.repositories.ProfissionalSaudeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Configuration
public class TesteJpaConfig {

    @Bean
    CommandLineRunner testeJpaRunner(TesteJpaService service) {
        return args -> service.executarTeste();
    }

    @Configuration
    static class TesteJpaService {
        private final PacienteRepository pacienteRepository;
        private final ProfissionalSaudeRepository profissionalSaudeRepository;
        private final ConsultaRepository consultaRepository;

        public TesteJpaService(PacienteRepository pacienteRepository,
                               ProfissionalSaudeRepository profissionalSaudeRepository,
                               ConsultaRepository consultaRepository) {
            this.pacienteRepository = pacienteRepository;
            this.profissionalSaudeRepository = profissionalSaudeRepository;
            this.consultaRepository = consultaRepository;
        }

        @Transactional
        public void executarTeste() {
            // Criando paciente
            Paciente paciente = new Paciente();
            paciente.setNomeCompleto("Bruna Silva");
            paciente.setDataDeNascimento(LocalDate.of(2004, 9, 5));
            paciente.setContato("123456789");
            paciente.setRua("Rua 3 irmãos");
            paciente.setBairro("Centro");
            paciente.setNumeroCasa(100);
            paciente.setResponsaveis(Arrays.asList("Pai", "Mãe", "Tia"));
            paciente.setTranstornos(Arrays.asList("TDAH", "Ansiedade"));

            pacienteRepository.save(paciente);
            System.out.println("Paciente salvo: " + paciente.getId());

            // Criando profissional
            ProfissionalSaude profissionalSaude = new ProfissionalSaude();
            profissionalSaude.setPrimeiroNome("Maria");
            profissionalSaude.setNomeCompleto("Maria De Souza");
            profissionalSaude.setContato("987654321");
            profissionalSaude.setEmail("maria@exemplo.com");
            profissionalSaude.setCrm("CRM-12345");

            profissionalSaudeRepository.save(profissionalSaude);
            System.out.println("Profissional salvo: " + profissionalSaude.getId());

            // ManyToMany
            List<Paciente> pacientes = new ArrayList<>();
            pacientes.add(paciente);
            profissionalSaude.setPacientes(pacientes);
            profissionalSaudeRepository.save(profissionalSaude);
            System.out.println("Relacionamento ManyToMany salvo");

            // Consulta
            Consulta consulta = new Consulta();
            consulta.setPaciente(paciente);
            consulta.setProfissional(profissionalSaude);
            consulta.setDataConsulta(LocalDateTime.now());
            consulta.setStatus(true);
            consulta.setRelatorio(Map.of(
                    "tema", "escuro",
                    "notificacoes", "true",
                    "linguagem", "pt-BR"
            ));
            consultaRepository.save(consulta);
            System.out.println("Consulta salva: " + consulta.getId());

            // Teste das relações (dentro da transação, sem LazyException)
            Paciente pacienteSalvo = pacienteRepository.findById(paciente.getId()).get();
            ProfissionalSaude profissionalSalvo = profissionalSaudeRepository.findById(profissionalSaude.getId()).get();

            System.out.println("\n--- Relações OneToMany e ManyToOne ---");
            System.out.println("Consultas do paciente " + pacienteSalvo.getNomeCompleto() + ":");
            if (pacienteSalvo.getConsultas() != null)
                pacienteSalvo.getConsultas().forEach(c ->
                        System.out.println("→ Consulta ID " + c.getId() + " com " + c.getProfissional().getNomeCompleto())
                );

            System.out.println("\nConsultas do profissional " + profissionalSalvo.getNomeCompleto() + ":");
            if (profissionalSalvo.getConsultas() != null)
                profissionalSalvo.getConsultas().forEach(c ->
                        System.out.println("→ Consulta ID " + c.getId() + " com paciente " + c.getPaciente().getNomeCompleto())
                );

            System.out.println("\n--- Relação ManyToMany ---");
            if (pacienteSalvo.getProfissionais() != null)
                pacienteSalvo.getProfissionais().forEach(p ->
                        System.out.println("Profissional vinculado: " + p.getNomeCompleto())
                );

            System.out.println("\n===== TESTE JPA FINALIZADO COM SUCESSO =====");
        }
    }
}