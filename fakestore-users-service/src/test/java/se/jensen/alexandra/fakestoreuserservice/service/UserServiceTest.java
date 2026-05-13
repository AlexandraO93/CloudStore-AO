package se.jensen.alexandra.fakestoreuserservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import se.jensen.alexandra.fakestoreuserservice.dto.user.UserRequestDTO;
import se.jensen.alexandra.fakestoreuserservice.dto.user.UserResponseDTO;
import se.jensen.alexandra.fakestoreuserservice.model.User;
import se.jensen.alexandra.fakestoreuserservice.repository.OrderRepository;
import se.jensen.alexandra.fakestoreuserservice.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    public void setUp() {
        orderRepository.deleteAll();
        userRepository.deleteAll();
    }

    // 1 Testar registrering av användare
    @Test
    public void addUser_shouldSaveUserWithHashedPassword() {
        // Arrange
        UserRequestDTO dto = new UserRequestDTO(1L, "Test", "Test", "test@mail.com", "password123", "password123", "adress", "0701234567");

        // Act
        UserResponseDTO result = userService.addUser(dto);

        // Assert
        assertEquals("test@mail.com", result.email());
        User savedUser = userRepository.findById(result.id()).orElseThrow();

        // Verifiera att lösenordet är hashat
        assertTrue(passwordEncoder.matches("password123", savedUser.getPassword()));
        assertNotEquals("password123", savedUser.getPassword());
    }

    // 2 Testar exception vid försök att skapa användare med email som redan finns i databasen
    @Test
    public void addUser_withExistingEmail_shouldThrowException() {
        // Arrange
        User existingUser = new User();
        existingUser.setFirstName("Test");
        existingUser.setLastName("Test");
        existingUser.setEmail("test@mail.com");
        existingUser.setPassword("password123");
        existingUser.setAddress("Testvägen 1");
        existingUser.setPhone("0701234567");
        userRepository.save(existingUser);

        UserRequestDTO dto = new UserRequestDTO(1L, "Test", "Test", "test@mail.com", "password123", "password123", "Testvägen 1", "0701234567");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.addUser(dto));
    }

    // 3 Test för att säkerställa att vi får felmeddelande om användaren saknas
    @Test
    public void getUserById_shouldThrowException_whenNotFound() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.getUserById(999L));
    }

    // 4 Hämtar användare, verifierar rätt förnamn och mail
    @Test
    public void getUserById_shouldReturnUser_whenFound() {
        // Arrange
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setEmail("test@mail.com");
        user.setPassword("password123");
        user.setAddress("Testvägen 1");
        user.setPhone("0701234567");
        User saved = userRepository.save(user);

        // Act
        UserResponseDTO result = userService.getUserById(saved.getId());

        // Assert
        assertEquals("Test", result.firstName());
        assertEquals("test@mail.com", result.email());
    }

    // 5 Testar att det funkar att ta bort en användare
    @Test
    public void deleteUser_shouldRemoveFromDatabase() {
        // Arrange
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setEmail("test@mail.com");
        user.setPassword("password123");
        user.setAddress("Testvägen 1");
        user.setPhone("0701234567");
        User saved = userRepository.save(user);

        // Act
        userService.deleteUser(saved.getId());

        // Assert
        assertFalse(userRepository.existsById(saved.getId()));
    }

    // 6 Test för uppdatering av användarinformation
    @Test
    public void updateUser_shouldChangeAddress() {
        // Arrange
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setEmail("update@mail.com");
        user.setPassword("password123");
        user.setAddress("Gamla Gatan 1");
        user.setPhone("0701234567");
        User saved = userRepository.save(user);

        UserRequestDTO updateDto = new UserRequestDTO(saved.getId(), "Test", "Test", "update@mail.com", "password123", "password123", "Nya Vägen 2", "0701234567");

        // Act
        userService.updateUser(updateDto, saved.getId());

        // Assert
        User updatedUser = userRepository.findById(saved.getId()).orElseThrow();
        assertEquals("Nya Vägen 2", updatedUser.getAddress());
    }
}