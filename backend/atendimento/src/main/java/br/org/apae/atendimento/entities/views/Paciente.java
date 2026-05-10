package br.org.apae.atendimento.entities.views;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

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

    @Column(name = "is_apagado")
    private Boolean isApagado;

    @Transient
    private String fotoPreAssinada;
}
// REMOVIDO: @OneToMany atendimentos    → use AtendimentoRepository
// REMOVIDO: @ManyToMany profissionais  → contradição com @Immutable
// REMOVIDO: Set<String> transtornos    → virá de vw_transtornos_paciente
// REMOVIDO: responsaveis — vem de vw_responsaveis_paciente, não desta view
// REMOVIDO: cidade/rua/bairro/numeroCasa — vem de vw_enderecos_paciente
