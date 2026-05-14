package br.org.apae.atendimento.entities.views;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.util.UUID;

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

    @Column(name = "nome")
    private String nomeCompleto;

    @Column(name = "email")
    private String email;

    @Column(name = "status")
    private String status;

    @Column(name = "contato")
    private String contato;

    @Column(name = "especialidade_id")
    private String especialidadeId; // chave de ligação — útil para filtros

    @Column(name = "especialidade")
    private String especialidade;   // nome legível — útil para exibição
}
    // REMOVIDO: senha  → CredenciaisProfissional.java
    // REMOVIDO: perfil → CredenciaisProfissional.java
    // REMOVIDO: @ManyToMany pacientes → contradição com @Immutable
    // REMOVIDO: @OneToMany atendimentos → usar AtendimentoRepository
    // REMOVIDO: @OneToMany arquivos → usar ArquivoRepository