package br.org.apae.atendimento.entities;
import jakarta.persistence.*;

@Entity
@Table(name = "relatorio")
public class Relatorio{

    public Relatorio(){}

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;
   
    @Column(name = "id_arquivo")
    private Long idArquivo;

    @ManyToOne 
    @JoinColumn(name = "profissional_id")
    private ProfissionalSaude profissional; 

    @ManyToOne
     @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdArquivo() {
        return idArquivo;
    }

    public void setIdArquivo(Long idArquivo) {
        this.idArquivo = idArquivo;
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
}