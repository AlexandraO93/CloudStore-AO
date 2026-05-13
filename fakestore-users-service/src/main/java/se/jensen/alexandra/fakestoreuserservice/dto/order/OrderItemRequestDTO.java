package se.jensen.alexandra.fakestoreuserservice.dto.order;

public record OrderItemRequestDTO(
        Long productId,
        Integer quantity
) {
}
