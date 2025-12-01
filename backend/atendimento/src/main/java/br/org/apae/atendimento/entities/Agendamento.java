package br.org.apae.atendimento.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "agendamento")
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

    @ManyToOne
    @JoinColumn(name = "profissional_id")
    private ProfissionalSaude profissional;

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    public Agendamento() {
    }

    public Agendamento(UUID id, ProfissionalSaude profissionalSaude, Paciente paciente, boolean status) {
        this.id = id;
        this.profissional = profissionalSaude;
        this.paciente = paciente;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public ProfissionalSaude getProfissional() {
        return profissional;
    }

    public void setProfissional(ProfissionalSaude profissional) {
        this.profissional = profissional;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Long getNumeracao() {
        return numeracao;
    }

    public void setNumeracao(Long numeracao) {
        this.numeracao = numeracao;
    }
}
