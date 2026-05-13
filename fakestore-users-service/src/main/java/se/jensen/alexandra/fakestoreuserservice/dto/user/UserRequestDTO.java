package se.jensen.alexandra.fakestoreuserservice.dto.user;

public record UserRequestDTO(
        Long id,
        String firstName,
        String lastName,
        String email,
        String password,
        String currentPassword,
        String address,
        String phone
) {
}
