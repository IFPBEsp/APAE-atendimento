package br.org.apae.atendimento.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "agendamento", schema = "atendimento")
@Getter
@Setter
@NoArgsConstructor
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "status")
    private boolean status;

    @Column(name = "data_hora")
    private LocalDateTime dataHora;

    @Column(name = "numeracao")
    private Long numeracao;

    @Column(name = "profissional_id")
    private UUID profissionalId;

    @Column(name = "paciente_id")
    private UUID pacienteId;

    public Agendamento(UUID id, UUID profissionalId, UUID pacienteId, boolean status) {
        this.id = id;
        this.profissionalId = profissionalId;
        this.pacienteId = pacienteId;
        this.status = status;
    }
}