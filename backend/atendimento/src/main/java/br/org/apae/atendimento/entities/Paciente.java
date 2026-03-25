package br.org.apae.atendimento.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.Immutable; // IMPORTANTE
import java.time.LocalDate;
import java.util.*;

@Entity
@Immutable // SOMENTE LEITURA (View)
@Table(name = "vw_pacientes")
public class Paciente {

    @Id
    private UUID id;

    @Column(name = "nome")
    private String nomeCompleto;

    @Column(name = "data_nascimento")
    private LocalDate dataDeNascimento;

    @Column(name = "cpf")
    private String cpf;

    // A issue deixa claro os atributos selecionados, mas estou mantendo estes temporariamente.

    @Transient
    private String contato;

    @Transient
    private List<String> responsaveis = new ArrayList<>();

    @Transient
    private String cidade;

    @Transient
    private String rua;

    @Transient
    private String bairro;

    @Transient
    private Integer numeroCasa;

    @Transient
    private String fotoPreAssinada;

    @Transient
    private List<String> transtornos = new ArrayList<>();

    @OneToMany(mappedBy = "paciente")
    private Set<Atendimento> atendimentos = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "pacientes")
    private Set<ProfissionalSaude> profissionais = new HashSet<>();

    public Paciente() {}

    public Paciente(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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

    public Set<Atendimento> getAtendimentos() {
        return atendimentos;
    }

    public String getCidade() {
        return cidade;
    }

    public void setAtendimentos(Set<Atendimento> atendimentos) {
        this.atendimentos = atendimentos;
    }

    public Set<ProfissionalSaude> getProfissionais() {
        return profissionais;
    }

    public void setProfissionais(Set<ProfissionalSaude> profissionais) {
        this.profissionais = profissionais;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getFotoPreAssinada() {
        return fotoPreAssinada;
    }

    public void setFotoPreAssinada(String fotoPreAssinada) {
        this.fotoPreAssinada = fotoPreAssinada;
    }
}
