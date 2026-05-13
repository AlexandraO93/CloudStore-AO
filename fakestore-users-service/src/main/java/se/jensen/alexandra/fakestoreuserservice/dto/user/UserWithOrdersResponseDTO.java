package se.jensen.alexandra.fakestoreuserservice.dto.user;

import se.jensen.alexandra.fakestoreuserservice.dto.order.OrderResponseDTO;

import java.util.List;

public record UserWithOrdersResponseDTO(
        UserResponseDTO user,
        List<OrderResponseDTO> orders
) {
}
