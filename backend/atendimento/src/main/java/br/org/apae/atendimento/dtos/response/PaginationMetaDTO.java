package br.org.apae.atendimento.dtos.response;

public record PaginationMetaDTO(
         int page,
         int limit,
         long totalItems,
         int totalPages,
         boolean hasNextPage,
         boolean hasPreviousPage) {
}