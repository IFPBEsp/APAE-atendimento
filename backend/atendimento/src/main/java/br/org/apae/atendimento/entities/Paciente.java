package br.org.apae.atendimento.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "paciente")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_completo")
    private String nomeCompleto;

    @Column(name = "data_de_nascimento")
    private LocalDate dataDeNascimento;

    @Column(name = "contato")
    private String contato;

    @Column(name = "responsaveis")
    private List<String> responsaveis = new ArrayList<>();

    @Column(name = "cidade")
    private String cidade;

    @Column(name = "rua")
    private String rua;

    @Column(name = "bairro")
    private String bairro;

    @Column(name = "numero_casa")
    private Integer numeroCasa;

    @Column(name = "transtornos")
    private List<String> transtornos = new ArrayList<>();

    @OneToMany(mappedBy = "paciente")
    private List<Atendimento> atendimentos = new ArrayList<>();

    @ManyToMany(mappedBy = "pacientes")
    private List<ProfissionalSaude> profissionais = new ArrayList<>();

    public Paciente() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public LocalDate getDataDeNascimento() {
        return dataDeNascimento;
    }

    public void setDataDeNascimento(LocalDate dataDeNascimento) {
        this.dataDeNascimento = dataDeNascimento;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public List<String> getResponsaveis() {
        return responsaveis;
    }

    public void setResponsaveis(List<String> responsaveis) {
        this.responsaveis = responsaveis;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public Integer getNumeroCasa() {
        return numeroCasa;
    }

    public void setNumeroCasa(Integer numeroCasa) {
        this.numeroCasa = numeroCasa;
    }

    public List<String> getTranstornos() {
        return transtornos;
    }

    public void setTranstornos(List<String> transtornos) {
        this.transtornos = transtornos;
    }

    public List<Atendimento> getAtendimentos() {
        return atendimentos;
    }

    public void setAtendimentos(List<Atendimento> atendimentos) {
        this.atendimentos = atendimentos;
    }

    public List<ProfissionalSaude> getProfissionais() {
        return profissionais;
    }

    public void setProfissionais(List<ProfissionalSaude> profissionais) {
        this.profissionais = profissionais;
    }
}
