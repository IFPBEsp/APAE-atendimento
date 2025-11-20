package br.org.apae.atendimento.entities;
import br.org.apae.atendimento.entities.interfaces.ArquivoStorage;
import jakarta.persistence.*;

@Entity
@Table(name = "relatorio")
public class Relatorio implements ArquivoStorage {

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

    @Override
    public String getBucket() {
        return bucket;
    }

    @Override
    public String getPresignedUrl() {
        return this.url;
    }

    @Override
    public void setPresignedUrl(String presignedUrl) {
        this.url = presignedUrl;
    }
}