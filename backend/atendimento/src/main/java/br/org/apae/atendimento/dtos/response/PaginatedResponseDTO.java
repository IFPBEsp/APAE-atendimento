package br.org.apae.atendimento.dtos.response;

import java.util.List;

public record PaginatedResponseDTO<T>(
        List<T> data,
        PaginationMetaDTO paginationMetaDTO
) {
}