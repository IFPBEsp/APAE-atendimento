package br.org.apae.atendimento.entities.views;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.util.UUID;

// Uma linha por paciente (GROUP BY no SQL da view).
// paciente_id é PK natural aqui.
@Entity
@Immutable
@Table(name = "vw_transtornos_paciente", schema = "atendimento")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TranstornoPaciente {

    @Id
    @Column(name = "paciente_id")
    private UUID pacienteId;

    // Coluna direta de apae.pacientes — campo String
    @Column(name = "alergias")
    private String alergias;

    // STRING_AGG dos transtornos do cadastro anual mais recente
    // ex: "Autismo, TDAH, Síndrome de Down"
    @Column(name = "transtornos")
    private String transtornos;
}