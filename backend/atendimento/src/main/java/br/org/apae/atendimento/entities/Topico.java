package br.org.apae.atendimento.entities;

import br.org.apae.atendimento.dtos.request.TopicoRequestDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "topico", schema = "atendimento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Topico {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "O título do tópico é obrigatório")
    @Column(nullable = false)
    private String titulo;

    @NotBlank(message = "A descrição do tópico é obrigatória")
    @Column(nullable = false)
    private String descricao;

    @ManyToOne(optional = false)
    @JoinColumn(name = "atendimento_id", nullable = false)
    private Atendimento atendimento;

    public static Topico from(TopicoRequestDTO dto, Atendimento atendimento) {
        Topico topico = new Topico();
        topico.setTitulo(dto.titulo());
        topico.setDescricao(dto.descricao());
        topico.setAtendimento(atendimento);
        return topico;
    }
}