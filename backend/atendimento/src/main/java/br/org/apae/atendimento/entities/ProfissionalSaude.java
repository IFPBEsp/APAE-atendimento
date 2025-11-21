package br.org.apae.atendimento.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "profissional_saude")
public class ProfissionalSaude {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "primeiro_nome")
    private String primeiroNome;

    @Column(name = "nome_completo")
    private String nomeCompleto;

    @Column(name = "email")
    private String email;

    @Column(name = "contato")
    private String contato;

    @Column(name = "crm")
    private String crm;

    @ManyToMany()
    @JoinTable(
            name = "profissional_paciente",
            joinColumns = @JoinColumn(name = "profissional_id"),
            inverseJoinColumns = @JoinColumn(name = "paciente_id")
    )
    private List<Paciente> pacientes = new ArrayList<>();

    @OneToMany(mappedBy = "profissional")
    private List<Atendimento> atendimentos = new ArrayList<>();

    @OneToMany(mappedBy = "profissional")
    private List<Anexo> anexos = new ArrayList<>();

    @OneToMany(mappedBy = "profissional")
    private List<Relatorio> relatorios = new ArrayList<>();

    public ProfissionalSaude() {}

    public ProfissionalSaude(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrimeiroNome() {
        return primeiroNome;
    }

    public void setPrimeiroNome(String primeiroNome) {
        this.primeiroNome = primeiroNome;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCrm() {
        return crm;
    }

    public void setCrm(String crm) {
        this.crm = crm;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public List<Paciente> getPacientes() {
        return pacientes;
    }

    public void setPacientes(List<Paciente> pacientes) {
        this.pacientes = pacientes;
    }

    public List<Atendimento> getAtendimentos() {
        return atendimentos;
    }

    public void setAtendimentos(List<Atendimento> atendimentos) {
        this.atendimentos = atendimentos;
    }
}
