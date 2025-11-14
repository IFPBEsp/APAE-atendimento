package br.org.apae.atendimento.entities;

@Entity
@Table(name = relatorio)
public class Relatorio{

    public Relatorio(){}

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;
   
    @Column(name = "id_arquivo")
    private Long idArquivo


    @ManyToOne 
    @JoinColumn(name = "profissional_id")
    private ProfissionalSaude profissional; 

    @ManyToOne
     @JoinColumn(name = "paciente_id")
    private Paciente paciente; 
}