package br.org.apae.atendimento.entities.views;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.util.UUID;

@Entity
@Immutable
@Table(name = "vw_enderecos_paciente", schema = "atendimento")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoPaciente {

    @Id
    @Column(name = "paciente_id")
    private UUID pacienteId;

    @Column(name = "cidade")
    private String cidade;

    @Column(name = "rua")
    private String rua;

    @Column(name = "bairro")
    private String bairro;

    @Column(name = "numero_casa")
    private String numeroCasa;
}