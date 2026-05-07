package se.jensen.alexandra.fakestoreuserservice.mapper;

import org.springframework.stereotype.Component;
import se.jensen.alexandra.fakestoreuserservice.dto.UserRequestDTO;
import se.jensen.alexandra.fakestoreuserservice.dto.UserResponseDTO;
import se.jensen.alexandra.fakestoreuserservice.model.User;

@Component
public class UserMapper {

    //Används vid skapande av ny kund
    public User fromDto(UserRequestDTO dto) {
        User user = new User();
        setUserValues(user, dto);
        return user;
    }

    //Används vid uppdatering av kund
    public User fromDto(User user, UserRequestDTO dto) {
        setUserValues(user, dto);
        return user;
    }

    private void setUserValues(User user, UserRequestDTO dto) {
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        user.setAddress(dto.address());
        user.setPhone(dto.phone());
    }

    public UserResponseDTO toDto(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getAddress(),
                user.getPhone()
        );
    }
}
