package br.org.apae.atendimento.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "relatorio")
public class Relatorio{

    public Relatorio(){}

    @Id
    private String id;

    private String bucket;

    @Transient
    private String url;

    @ManyToOne 
    @JoinColumn(name = "profissional_id")
    private ProfissionalSaude profissional; 

    @ManyToOne
     @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    public String getObjectName() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
    public String getBucket() {
        return bucket;
    }
    public String getPresignedUrl() {
        return this.url;
    }
    public void setPresignedUrl(String presignedUrl) {
        this.url = presignedUrl;
    }
}