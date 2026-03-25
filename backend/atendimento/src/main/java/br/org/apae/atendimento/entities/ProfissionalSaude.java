package br.org.apae.atendimento.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.Immutable; // IMPORTANTE
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Immutable // Somente Leitura
@Table(name = "vw_profissionais") // Aponta para a view exigida pelo PO
public class ProfissionalSaude {

    @Id
    private UUID id;

    @Column(name = "nome") // Mapeado para a View
    private String nomeCompleto;

    // Campos novos exigidos pela Issue
    @Column(name = "registro_profissional")
    private String registroProfissional;

    @Column(name = "especialidade")
    private String especialidade;

    @Column(name = "firebase_uid", unique = true)
    private String firebaseUID; // Mantido pois é vital para o AuthFilter

    // --- CAMPOS IGNORADOS PELO BANCO DE DADOS (@Transient) ---
    @Transient
    private String primeiroNome;

    @Transient
    private String email;

    @Transient
    private String contato;
    // ---------------------------------------------------------

    @JsonIgnore
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
    private List<Arquivo> arquivos = new ArrayList<>();
    public ProfissionalSaude() {}

    public ProfissionalSaude(UUID id) {
        this.id = id;
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getRegistroProfissional() { return registroProfissional; }
    public void setRegistroProfissional(String registroProfissional) { this.registroProfissional = registroProfissional; }

    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }

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

    public List<Arquivo> getArquivos() {
        return arquivos;
    }

    public void setArquivos(List<Arquivo> arquivos) {
        this.arquivos = arquivos;
    }

    public String getFirebaseUID() {
        return firebaseUID;
    }

    public void setFirebaseUID(String firebaseUID) {
        this.firebaseUID = firebaseUID;
    }
}
