public br.org.apae.atendimento.dtos.request.create;

public record AnexoRequestCreateDTO(
    String nomeAnexo,
    String descricao,
    Long pacienteId,
    Long arquivoId
){}

/// adicionar profissionalId compensa?