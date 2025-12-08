package br.org.apae.atendimento.dtos.request.update;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record AtendimentoUpdateRequestDTO(
        UUID profissionalId,
        UUID pacienteId,
        UUID atendimentoId,
        String tituloAntigo,
        Map<String, String> update) {
}
