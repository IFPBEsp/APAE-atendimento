package br.org.apae.atendimento.entities.views;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.util.UUID;

// ATENÇÃO: esta view pode retornar múltiplas linhas por paciente
// (um paciente pode ter N responsáveis).
// O @Id aqui é paciente_id + nome — use consultas por paciente_id
// em vez de findById para evitar colisão.
@Entity
@Immutable
@Table(name = "vw_responsaveis_paciente", schema = "atendimento")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponsavelPaciente {

    // Views com múltiplas linhas por "chave natural" precisam de
    // uma PK artificial para o Hibernate. Usamos UUID gerado.
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", insertable = false, updatable = false)
    private UUID id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "paciente_id")
    private UUID pacienteId;
}