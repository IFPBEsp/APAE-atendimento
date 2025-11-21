package br.org.apae.atendimento.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;


@Entity
@Table(name = "anexo")
public class Arquivo {

    @Id
    private String objectName;

    @Column(name = "bucket")
    private String bucket;

    @Transient
    private String presignedUrl;

    @Column(name = "nome_arquivo")
    private String nomeArquivo;


    @Column(name = "data")
    private LocalDate data;

    @ManyToOne
    @JoinColumn(name = "tipo_id")
    private TipoArquivo tipo;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "profissional_id")
    private ProfissionalSaude profissional;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    public Arquivo(){}

    public Arquivo(String objectName, String bucket, String nomeAnexo,
                   Paciente paciente, ProfissionalSaude profissional,
                   LocalDate data, TipoArquivo tipoArquivo, String url) {
        this.objectName = objectName;
        this.bucket = bucket;
        this.nomeArquivo = nomeAnexo;
        this.paciente = paciente;
        this.profissional = profissional;
        this.data = data;
        this.tipo = tipoArquivo;
        this.presignedUrl = url;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
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
        return presignedUrl;
    }
    public void setPresignedUrl(String presignedUrl) {
        this.presignedUrl = presignedUrl;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public TipoArquivo getTipo() {
        return tipo;
    }

    public void setTipo(TipoArquivo tipo) {
        this.tipo = tipo;
    }
}