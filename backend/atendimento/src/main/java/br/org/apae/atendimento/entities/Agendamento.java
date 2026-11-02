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

    @Column(name = "profissional_id", nullable = false)
    private UUID profissionalId;

    @Column(name = "paciente_id", nullable = false)
    private UUID pacienteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profissional_id", insertable = false, updatable = false)
    private ProfissionalSaude profissional;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", insertable = false, updatable = false)
    private Paciente paciente;
}