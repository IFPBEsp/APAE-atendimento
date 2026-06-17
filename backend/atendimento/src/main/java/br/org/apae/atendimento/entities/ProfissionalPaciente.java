package br.org.apae.atendimento.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.*;

@Entity
@Table(name = "profissional_paciente", schema = "atendimento")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(ProfissionalPacienteId.class)
public class ProfissionalPaciente {

    @Id
    @Column(name = "profissional_id")
    private UUID profissionalId;

    @Id
    @Column(name = "paciente_id")
    private UUID pacienteId;
}