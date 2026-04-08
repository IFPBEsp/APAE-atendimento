package br.org.apae.atendimento.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Entity
@Table(name = "anexo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Arquivo {

    @Id
    private String objectName;

    @Transient
    private String presignedUrl;

    @Column(name = "nome_arquivo")
    private String nomeArquivo;

    @Column(name = "data")
    private LocalDate data;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "descricao")
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "tipo_id")
    private TipoArquivo tipo;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "profissional_id")
    private ProfissionalSaude profissional;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;
}