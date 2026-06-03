package br.org.apae.atendimento.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.*;

@Entity
@Immutable
@Table(name = "vw_pacientes", schema = "atendimento")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Paciente {

    @Id
    @Column(name = "paciente_id")
    private UUID id;

    @Column(name = "nome")
    private String nomeCompleto;

    @Column(name = "data_nascimento")
    private LocalDate dataDeNascimento;

    @Column(name = "cpf")
    private String cpf;

    @Column(name = "contato")
    private String contato;

    @Column(name = "cidade")
    private String cidade;

    @Column(name = "rua")
    private String rua;

    @Column(name = "bairro")
    private String bairro;

    @Column(name = "numero_casa")
    private Integer numeroCasa;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "responsaveis")
    private Set<String> responsaveis = new HashSet<>();

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "transtornos")
    private Set<String> transtornos = new HashSet<>();

    @Transient
    @JsonIgnore
    private String fotoPreAssinada;

}
