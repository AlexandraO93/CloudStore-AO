package se.jensen.alexandra.fakestoreuserservice.dto.user;

public record UserResponseDTO(
        Long id,
        String firstName,
        String lastName,
        String email,
        String address,
        String phone
) {
}
