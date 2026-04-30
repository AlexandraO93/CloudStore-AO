package se.jensen.alexandra.fakestoreuserservice.dto;

import java.util.List;

public record UserWithOrdersResponseDTO(
        UserResponseDTO user,
        List<OrderResponseDTO> orders
) {
}
