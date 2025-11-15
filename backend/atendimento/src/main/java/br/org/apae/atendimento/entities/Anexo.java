package br.org.apae.atendimento.entities;
import jakarta.persistence.*;


@Entity
@Table(name = "anexo")
public class Anexo {

    public Anexo(){}

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_arquivo")
    private Long idArquivo;


    @Column(name = "nome_anexo")
    private String nomeAnexo;

    @Column(name = "descricao")
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "profissional_id")
    private ProfissionalSaude profissional; 

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente; 
}