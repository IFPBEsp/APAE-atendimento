package br.org.apae.atendimento.config;

import br.org.apae.atendimento.entities.Atendimento;
import br.org.apae.atendimento.entities.Paciente;
import br.org.apae.atendimento.entities.ProfissionalSaude;
import br.org.apae.atendimento.entities.TipoArquivo;
import br.org.apae.atendimento.repositories.AtendimentoRepository;
import br.org.apae.atendimento.repositories.PacienteRepository;
import br.org.apae.atendimento.repositories.ProfissionalSaudeRepository;
import br.org.apae.atendimento.repositories.TipoArquivoRepository;
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
        private final AtendimentoRepository atendimentoRepository;
        private final TipoArquivoRepository tipoArquivoRepository;

        public TesteJpaService(PacienteRepository pacienteRepository,
                               ProfissionalSaudeRepository profissionalSaudeRepository,
                               AtendimentoRepository atendimentoRepository,
                               TipoArquivoRepository tipoArquivoRepository) {
            this.pacienteRepository = pacienteRepository;
            this.profissionalSaudeRepository = profissionalSaudeRepository;
            this.atendimentoRepository = atendimentoRepository;
            this.tipoArquivoRepository = tipoArquivoRepository;
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

            // Atendimento
            Atendimento atendimento = new Atendimento();
            atendimento.setPaciente(paciente);
            atendimento.setProfissional(profissionalSaude);
            atendimento.setDataAtendimento(LocalDateTime.now());
            atendimento.setStatus(true);
            atendimento.setRelatorio(Map.of(
                    "tema", "escuro",
                    "notificacoes", "true",
                    "linguagem", "pt-BR"
            ));
            atendimentoRepository.save(atendimento);
            System.out.println("Atendimento salva: " + atendimento.getId());

            // Teste das relações (dentro da transação, sem LazyException)
            Paciente pacienteSalvo = pacienteRepository.findById(paciente.getId()).get();
            ProfissionalSaude profissionalSalvo = profissionalSaudeRepository.findById(profissionalSaude.getId()).get();

            System.out.println("\n--- Relações OneToMany e ManyToOne ---");
            System.out.println("Atendimentos do paciente " + pacienteSalvo.getNomeCompleto() + ":");
            if (pacienteSalvo.getAtendimentos() != null)
                pacienteSalvo.getAtendimentos().forEach(c ->
                        System.out.println("→ Atendimento ID " + c.getId() + " com " + c.getProfissional().getNomeCompleto())
                );

            System.out.println("\nAtendimentos do profissional " + profissionalSalvo.getNomeCompleto() + ":");
            if (profissionalSalvo.getAtendimentos() != null)
                profissionalSalvo.getAtendimentos().forEach(c ->
                        System.out.println("→ Atendimento ID " + c.getId() + " com paciente " + c.getPaciente().getNomeCompleto())
                );

            System.out.println("\n--- Relação ManyToMany ---");
            if (pacienteSalvo.getProfissionais() != null)
                pacienteSalvo.getProfissionais().forEach(p ->
                        System.out.println("Profissional vinculado: " + p.getNomeCompleto())
                );

            System.out.println("\n===== INICIALIZAÇÃO DE TIPOS DE ARQUIVOS =====");

            TipoArquivo tipoArquivo1 = new TipoArquivo(1L, "anexo");
            tipoArquivoRepository.save(tipoArquivo1);
            TipoArquivo tipoArquivo2 = new TipoArquivo(2L, "relatório");
            tipoArquivoRepository.save(tipoArquivo2);
            System.out.println("\n===== TESTE JPA FINALIZADO COM SUCESSO =====");
        }
    }
}