package br.org.apae.atendimento.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;


@Entity
@Table(name = "anexo", schema = "atendimento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Arquivo {

    @Id
    private String objectName;

    @Transient
    private String presignedUrl;

    @Column(name = "nome_arquivo", nullable = false)
    private String nomeArquivo;

    @Column(name = "data", nullable = false)
    private LocalDate data;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "descricao", nullable = false)
    private String descricao;

    @Column(name = "titulo_canonical")
    private String tituloCanonical;

    @ManyToOne
    @JoinColumn(name = "tipo_id", nullable = false)
    private TipoArquivo tipo;

    @Column(name = "profissional_id", nullable = false)
    private UUID profissionalId;

    @Column(name = "paciente_id", nullable = false)
    private UUID pacienteId;
}