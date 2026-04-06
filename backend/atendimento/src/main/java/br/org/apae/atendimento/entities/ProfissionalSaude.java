package br.org.apae.atendimento.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable; // IMPORTANTE

import java.util.*;

@Entity
@Immutable // Somente Leitura
@Table(name = "vw_profissionais") // Aponta para a view exigida pelo PO
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProfissionalSaude {

    @Id
    private UUID id;

    @Column(name = "nome") // Mapeado para a View
    private String nomeCompleto;

    @Column(name = "registro_profissional")
    private String registroProfissional;

    @Column(name = "especialidade")
    private String especialidade;

    @Column(name = "firebase_uid", unique = true)
    private String firebaseUID;

    @Column(name = "email")
    private String email;

    @Column(name = "status")
    private String status;

    // --- CAMPOS IGNORADOS PELO BANCO DE DADOS (@Transient) ---
    @Transient
    private String primeiroNome;

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
    private Set<Paciente> pacientes = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "profissional")
    private Set<Atendimento> atendimentos = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "profissional")
    private Set<Arquivo> arquivos = new HashSet<>();

}
