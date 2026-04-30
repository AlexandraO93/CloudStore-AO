package se.jensen.alexandra.fakestoreuserservice.dto;

public record UserRequestDTO(
        Long id,
        String firstName,
        String lastName,
        String email,
        String password,
        String address,
        String phone
) {
}
