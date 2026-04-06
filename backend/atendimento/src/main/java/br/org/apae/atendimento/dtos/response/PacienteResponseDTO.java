package br.org.apae.atendimento.dtos.response;
import java.util.Set;
import java.util.UUID;
import java.time.LocalDate;

public record PacienteResponseDTO(
        UUID id,
        String nomeCompleto,
        LocalDate dataDeNascimento,
        String endereco,
        String contato,
        Set<String> responsaveis,
        Set<String> transtornos,
        String cpf,
        String fotoPreAssinada
) {
    public PacienteResponseDTO comFoto(String novaFoto) {
        return new PacienteResponseDTO(
                this.id,
                this.nomeCompleto,
                this.dataDeNascimento,
                this.endereco,
                this.contato,
                this.responsaveis,
                this.transtornos,
                this.cpf,
                novaFoto
        );
    }
}
