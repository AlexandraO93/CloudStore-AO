package se.jensen.alexandra.fakestoreuserservice.dto;

import java.util.List;

public record OrderRequestDTO(
        Long customerId,
        List<OrderItemRequestDTO> items
) {
}
