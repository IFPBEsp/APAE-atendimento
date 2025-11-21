package br.org.apae.atendimento.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "atendimento")
public class Atendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "relatorio", columnDefinition = "json")
    private Map<String, Object> relatorio;

    @Column(name = "data_atendimento")
    private LocalDateTime dataAtendimento;

    @Column(name = "status")
    private boolean status;

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "profissional_id")
    private ProfissionalSaude profissional;

    public Atendimento() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, Object> getRelatorio() {
        return relatorio;
    }

    public void setRelatorio(Map<String, Object> relatorio) {
        this.relatorio = relatorio;
    }

    public LocalDateTime getDataAtendimento() {
        return dataAtendimento;
    }

    public void setDataAtendimento(LocalDateTime dataAtendimento) {
        this.dataAtendimento = dataAtendimento;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public ProfissionalSaude getProfissional() {
        return profissional;
    }

    public void setProfissional(ProfissionalSaude profissional) {
        this.profissional = profissional;
    }
}
