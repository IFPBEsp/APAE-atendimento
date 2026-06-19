package br.org.apae.atendimento.entities;

import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfissionalPacienteId implements Serializable {
    private UUID profissionalId;
    private UUID pacienteId;
}