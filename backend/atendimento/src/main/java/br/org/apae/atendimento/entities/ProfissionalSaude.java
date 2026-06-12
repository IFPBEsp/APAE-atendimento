package br.org.apae.atendimento.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.util.*;

@Entity
@Immutable
@Table(name = "vw_profissional_saude", schema = "atendimento")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProfissionalSaude {

    @Id
    @Column(name = "profissional_saude_id")
    private UUID id;

    @Column(name = "usuario_id")
    private UUID usuarioId;

    @Column(name = "nome")
    private String nomeCompleto;

    @Column(name = "cpf")
    private String cpf;

    @Column(name = "registro_profissional")
    private String registroProfissional;

    @Column(name = "especialidade")
    private String especialidade;

    @Column(name = "email")
    private String email;

    @JsonIgnore
    @Column(name = "senha")
    private String senha;

    @Column(name = "perfil")
    private String perfil;

    @Column(name = "ativo")
    private Boolean ativo;

    @Column(name = "primeiro_acesso")
    private Boolean primeiroAcesso;

    @Column(name = "contato")
    private String contato;

}
