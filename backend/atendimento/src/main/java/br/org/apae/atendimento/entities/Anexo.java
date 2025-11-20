package br.org.apae.atendimento.entities;
import br.org.apae.atendimento.entities.interfaces.ArquivoStorage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;


@Entity
@Table(name = "anexo")
public class Anexo implements ArquivoStorage {

    @Id
    private String id;

    @Column(name = "bucket")
    private String bucket;

    @Column(name = "url")
    @Transient
    private String url;

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

    public Anexo(String id, String bucket, String nomeAnexo, UUID pacienteId,
                 Long profissionalId, LocalDate data, String url) {
        this.id = id;
        this.bucket = bucket;
        this.nomeAnexo = nomeAnexo;
        this.paciente = new Paciente(pacienteId);
        this.profissional = new ProfissionalSaude(profissionalId);
        this.data = data;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
    public String getUrl() {
        return url;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }
}