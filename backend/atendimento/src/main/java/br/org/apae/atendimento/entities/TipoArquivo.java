package br.org.apae.atendimento.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tipo_arquivo", schema = "atendimento")
public class TipoArquivo {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "tipo")
    private String tipo;

}