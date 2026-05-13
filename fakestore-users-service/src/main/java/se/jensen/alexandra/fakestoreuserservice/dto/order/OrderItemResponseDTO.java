package se.jensen.alexandra.fakestoreuserservice.dto.order;

public record OrderItemResponseDTO(
        Long id,
        Long productId,
        Integer quantity,
        Double price
) {
}
