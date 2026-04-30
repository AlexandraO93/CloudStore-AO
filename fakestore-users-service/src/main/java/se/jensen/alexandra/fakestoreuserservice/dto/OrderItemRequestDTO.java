package se.jensen.alexandra.fakestoreuserservice.dto;

public record OrderItemRequestDTO(
        Long productId,
        Integer quantity
) {
}
