package br.org.apae.atendimento.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "atendimento", schema = "atendimento")
@Getter
@Setter
@NoArgsConstructor
public class Atendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToMany(mappedBy = "atendimento", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "ordem")
    private List<Topico> relatorio = new ArrayList<>();

    @Column(name = "data_atendimento")
    private LocalDateTime dataAtendimento;

    @Column(name = "numeracao")
    private String numeracao;

    @Column(name = "paciente_id", nullable = false)
    private UUID pacienteId;

    @Column(name = "profissional_id", nullable = false)
    private UUID profissionalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", insertable = false, updatable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profissional_id", insertable = false, updatable = false)
    private ProfissionalSaude profissional;

    @Column(name = "status")
    private boolean status;

}