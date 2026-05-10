package br.org.apae.atendimento.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;

import java.time.LocalDateTime;

@Entity
@Table(name = "credenciais_profissional", schema = "atendimento")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CredenciaisProfissional {

    @Id
    @Column(name = "profissional_id")
    private UUID profissionalId;

    @Column(name = "senha", nullable = false)
    private String senha;

    @Column(name = "perfil", nullable = false)
    private String perfil = "ROLE_PROFISSIONAL";

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    @PrePersist
    private void prePersist() {
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
