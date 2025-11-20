package br.org.apae.atendimento.entities;
import br.org.apae.atendimento.entities.interfaces.ArquivoStorage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "anexo")
public class Anexo implements ArquivoStorage {

    @Id
    private String objectName;

    @Column(name = "bucket")
    private String bucket;

    @Transient
    private String presignedUrl;

    @Column(name = "nome_anexo")
    private String nomeAnexo;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "data")
    private LocalDate data;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "profissional_id")
    private ProfissionalSaude profissional;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    public Anexo(){}

    public Anexo(String objectName) {
        this.objectName = objectName;
    }

    public Anexo(String id, String bucket, String nomeAnexo, UUID pacienteId,
                 Long profissionalId, LocalDate data, String url) {
        this.objectName = id;
        this.bucket = bucket;
        this.nomeAnexo = nomeAnexo;
        this.paciente = new Paciente(pacienteId);
        this.profissional = new ProfissionalSaude(profissionalId);
        this.data = data;
        this.presignedUrl = url;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getNomeAnexo() {
        return nomeAnexo;
    }

    public void setNomeAnexo(String nomeAnexo) {
        this.nomeAnexo = nomeAnexo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
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

    @Override
    public String getBucket() {
        return bucket;
    }

    @Override
    public String getPresignedUrl() {
        return presignedUrl;
    }

    @Override
    public void setPresignedUrl(String presignedUrl) {
        this.presignedUrl = presignedUrl;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }
}