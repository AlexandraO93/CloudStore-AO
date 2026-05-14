package se.jensen.alexandra.fakestoreuserservice.service;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import se.jensen.alexandra.fakestoreuserservice.dto.order.OrderItemRequestDTO;
import se.jensen.alexandra.fakestoreuserservice.dto.order.OrderRequestDTO;
import se.jensen.alexandra.fakestoreuserservice.dto.order.OrderResponseDTO;
import se.jensen.alexandra.fakestoreuserservice.dto.order.ProductDTO;
import se.jensen.alexandra.fakestoreuserservice.model.Order;
import se.jensen.alexandra.fakestoreuserservice.model.User;
import se.jensen.alexandra.fakestoreuserservice.repository.OrderRepository;
import se.jensen.alexandra.fakestoreuserservice.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @MockitoBean
    private RestTemplate restTemplate;

    private User testUser;

    @BeforeEach
    public void setUp() {
        orderRepository.deleteAll();
        userRepository.deleteAll();

        // Skapar en testanvändare från start
        testUser = new User();
        testUser.setFirstName("Order");
        testUser.setLastName("Tester");
        testUser.setEmail("order@test.com");
        testUser.setPassword("password123");
        testUser.setAddress("Testgatan 1");
        testUser.setPhone("0701234567");
        testUser = userRepository.save(testUser);

        // Simulerar en HTTP-request så RequestContextHolder inte är tom
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer fake-token");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    // 1 Testar att skapa en order
    @Test
    public void createOrder_shouldSaveOrder_whenProductExists() {
        // Arrange
        OrderItemRequestDTO itemDto = new OrderItemRequestDTO(101L, 2);
        OrderRequestDTO requestDto = new OrderRequestDTO(testUser.getId(), List.of(itemDto));

        ProductDTO mockProduct = new ProductDTO(101L, "Laptop", 5000.0, "Electronics", "Electronics", "");

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(ProductDTO.class)))
                .thenReturn(ResponseEntity.ok(mockProduct));

        // Act
        OrderResponseDTO result = orderService.createOrder(testUser.getId(), requestDto);

        // Assert
        assertNotNull(result);
        assertEquals(Order.Status.CREATED.toString(), result.status());
        assertFalse(result.items().isEmpty());
        assertEquals(5000.0, result.items().getFirst().price());
    }

    // 2 Testar felhantering om produkt saknas i den externa tjänsten
    @Test
    public void createOrder_shouldThrowException_whenProductNotFound() {
        // Arrange
        OrderItemRequestDTO itemDto = new OrderItemRequestDTO(999L, 1);
        OrderRequestDTO requestDto = new OrderRequestDTO(testUser.getId(), List.of(itemDto));

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(ProductDTO.class)))
                .thenReturn(ResponseEntity.ok(null));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(testUser.getId(), requestDto));
    }

    // 3 Testar att hämta order via ID
    @Test
    public void getOrderById_shouldReturnOrder() {
        // Arrange
        Order order = new Order();
        order.setUser(testUser);
        order.setStatus(Order.Status.CREATED);
        Order saved = orderRepository.save(order);

        // Act
        OrderResponseDTO result = orderService.getOrderById(saved.getOrderId());

        // Assert
        assertEquals(saved.getOrderId(), result.orderId());
    }

    // 4. Testar att uppdatera status
    @Test
    public void updateOrderStatus_shouldChangeStatus() {
        // Arrange
        Order order = new Order();
        order.setUser(testUser);
        order.setStatus(Order.Status.CREATED);
        Order saved = orderRepository.save(order);

        // Act
        orderService.updateOrderStatus(saved.getOrderId(), Order.Status.SHIPPED);

        // Assert
        Order updated = orderRepository.findById(saved.getOrderId()).get();
        assertEquals(Order.Status.SHIPPED, updated.getStatus());
    }

    // 5. Testar att man INTE kan ändra status på en redan avbruten order
    @Test
    public void updateOrderStatus_shouldThrowException_whenOrderIsCancelled() {
        // Arrange
        Order order = new Order();
        order.setUser(testUser);
        order.setStatus(Order.Status.CANCELLED);
        Order saved = orderRepository.save(order);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> orderService.updateOrderStatus(saved.getOrderId(), Order.Status.SHIPPED));
    }

    // 6. Testar att avbryta en order
    @Test
    public void cancelOrder_shouldSetStatusToCancelled() {
        // Arrange
        Order order = new Order();
        order.setUser(testUser);
        order.setStatus(Order.Status.CREATED);
        Order saved = orderRepository.save(order);

        // Act
        orderService.cancelOrder(saved.getOrderId());

        // Assert
        Order updated = orderRepository.findById(saved.getOrderId()).get();
        assertEquals(Order.Status.CANCELLED, updated.getStatus());
    }
}