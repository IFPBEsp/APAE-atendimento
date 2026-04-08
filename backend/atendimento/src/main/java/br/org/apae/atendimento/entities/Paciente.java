package br.org.apae.atendimento.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.*;

@Entity
@Immutable // SOMENTE LEITURA (View)
@Table(name = "vw_pacientes")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Paciente {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "nome")
    private String nomeCompleto;

    @Column(name = "data_nascimento")
    private LocalDate dataDeNascimento;

    @Column(name = "cpf")
    private String cpf;

    @Column(name = "contato")
    private String contato;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "responsaveis")
    private Set<String> responsaveis = new HashSet<>();

    @Column(name = "cidade")
    private String cidade;

    @Column(name = "rua")
    private String rua;

    @Column(name = "bairro")
    private String bairro;

    @Column(name = "numero_casa")
    private Integer numeroCasa;

    @Transient
    private String fotoPreAssinada;

    @Column(name = "ativo")
    private Boolean ativo = true;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "transtornos")
    private Set<String> transtornos = new HashSet<>();

    @OneToMany(mappedBy = "paciente")
    private Set<Atendimento> atendimentos = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "pacientes")
    private Set<ProfissionalSaude> profissionais = new HashSet<>();

}