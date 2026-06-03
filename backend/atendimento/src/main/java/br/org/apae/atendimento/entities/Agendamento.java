package br.org.apae.atendimento.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "agendamento", schema = "atendimento")
public class Agendamento {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "status")
    private boolean status;

    @Column(name = "data_hora")
    private LocalDateTime dataHora;

    @Column(name = "numeracao")
    private String numeracao;

    @ManyToOne
    @JoinColumn(name = "profissional_id")
    private ProfissionalSaude profissional;

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;
}