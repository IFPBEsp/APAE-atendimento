package br.org.apae.atendimento.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "atendimento", schema = "atendimento")
@Getter
@Setter
@NoArgsConstructor
public class Atendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "data_atendimento")
    private LocalDateTime dataAtendimento;

    @Column(name = "numeracao")
    private Long numeracao;

    @Column(name = "paciente_id", nullable = false)
    private UUID pacienteId;

    @Column(name = "profissional_id", nullable = false)
    private UUID profissionalId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "atendimento_id")
    private Set<Topico> relatorio = new HashSet<>();


}