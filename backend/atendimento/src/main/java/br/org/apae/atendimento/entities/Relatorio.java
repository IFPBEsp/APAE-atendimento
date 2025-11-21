package br.org.apae.atendimento.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "relatorio")
public class Relatorio{

    @Id
    @Column(name = "objectName")
    private String objectName;

    @Column(name = "bucket")
    private String bucket;

    @Transient
    @Column(name = "presigned_url")
    private String presignedUrl;

    @Column(name = "nome_arquivo")
    private String nomeArquivo;

    @ManyToOne
    @JoinColumn(name = "profissional_id")
    private ProfissionalSaude profissional;

    @ManyToOne
     @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    public Relatorio(){}

    public Relatorio(String objectName, String bucket,
                     Paciente paciente, ProfissionalSaude profissionalSaude, String url) {
        this.objectName = objectName;
        this.bucket = bucket;
        this.paciente = paciente;
        this.profissional = profissionalSaude;
        this.presignedUrl = url;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setId(String id) {
        this.objectName = id;
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
        return this.presignedUrl;
    }
    public void setPresignedUrl(String presignedUrl) {
        this.presignedUrl = presignedUrl;
    }
}