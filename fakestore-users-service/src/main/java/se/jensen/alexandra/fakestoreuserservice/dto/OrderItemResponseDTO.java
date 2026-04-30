package se.jensen.alexandra.fakestoreuserservice.dto;

public record OrderItemResponseDTO(
        Long id,
        Long productId,
        Integer quantity,
        Double price
) {
}
