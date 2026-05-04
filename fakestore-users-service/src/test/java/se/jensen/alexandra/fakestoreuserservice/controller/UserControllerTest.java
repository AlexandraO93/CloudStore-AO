package se.jensen.alexandra.fakestoreuserservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import se.jensen.alexandra.fakestoreuserservice.model.User;
import se.jensen.alexandra.fakestoreuserservice.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    // 1 Verifierar att man inte kan hämta användarlista utan att vara inloggad
    @Test
    public void getUserWithOrders_withoutToken_shouldReturn401() throws Exception {
        mockMvc.perform(get("/users/1/orders"))
                .andExpect(status().isUnauthorized());
    }

    // 2 Happy-path test
    @Test
    @WithMockUser
    public void getUserById_withToken_shouldReturnUserJson() throws Exception {
        // Arrange
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setEmail("test@mail.com");
        user.setPassword("password123");
        user.setAddress("Testvägen 1");
        user.setPhone("0701234567");
        User saved = userRepository.save(user);

        // Act & Assert
        mockMvc.perform(get("/users/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Verifierar 200 OK
                .andExpect(jsonPath("$.firstName").value("Test")) // Verifierar JSON-innehåll
                .andExpect(jsonPath("$.email").value("test@mail.com"));
    }
}
