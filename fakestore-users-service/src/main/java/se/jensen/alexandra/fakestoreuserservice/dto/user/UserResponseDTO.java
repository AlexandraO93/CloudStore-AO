package se.jensen.alexandra.fakestoreuserservice.dto.user;

import java.time.Instant;

public record UserResponseDTO(
        Long id,
        String firstName,
        String lastName,
        String email,
        String address,
        String phone,
        Instant createdAt,
        String createdByInstance
) {
}
