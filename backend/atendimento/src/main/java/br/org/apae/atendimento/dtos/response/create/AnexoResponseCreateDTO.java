public br.org.apae.atendimento.dtos.response.create;

public record AnexoResponseCreateDTO(
    Long id, 
    String nomeAnexo,
    String descricao
){}

//// Faz sentido sem as referências 