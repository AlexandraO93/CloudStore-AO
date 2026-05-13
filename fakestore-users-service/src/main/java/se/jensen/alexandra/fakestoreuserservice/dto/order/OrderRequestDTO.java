package se.jensen.alexandra.fakestoreuserservice.dto.order;

import java.util.List;

public record OrderRequestDTO(
        Long customerId,
        List<OrderItemRequestDTO> items
) {
}
