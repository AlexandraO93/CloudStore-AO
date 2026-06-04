package se.jensen.alexandra.fakestoreuserservice.controller.user;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.jensen.alexandra.fakestoreuserservice.dto.order.OrderRequestDTO;
import se.jensen.alexandra.fakestoreuserservice.dto.order.OrderResponseDTO;
import se.jensen.alexandra.fakestoreuserservice.dto.user.UserRequestDTO;
import se.jensen.alexandra.fakestoreuserservice.dto.user.UserResponseDTO;
import se.jensen.alexandra.fakestoreuserservice.dto.user.UserWithOrdersResponseDTO;
import se.jensen.alexandra.fakestoreuserservice.service.OrderService;
import se.jensen.alexandra.fakestoreuserservice.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final OrderService orderService;

    public UserController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> addUser
            (@Valid @RequestBody UserRequestDTO request) {
        log.info("Register user request received");
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.addUser(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser
            (@PathVariable Long id,
             @Valid @RequestBody UserRequestDTO request) {
        log.info("Update user request received for id={}", id);
        return ResponseEntity.ok().body(userService.updateUser(request, id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById
            (@PathVariable Long id) {
        log.debug("Get user by id={}", id);
        return ResponseEntity.ok().body(userService.getUserById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser
            (@PathVariable Long id) {
        log.info("Delete user request for id={}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/orders")
    public ResponseEntity<OrderResponseDTO> createOrderForUser(
            @PathVariable Long id,
            @Valid @RequestBody OrderRequestDTO orderRequestDTO) {
        log.info("Create order requested for userId={} items={}", id, orderRequestDTO.items().size());
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(id, orderRequestDTO));
    }

    @GetMapping("/{id}/orders")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrdersByUser(
            @PathVariable Long id) {
        log.debug("Get all orders for userId={}", id);
        return ResponseEntity.ok().body(orderService.getAllOrdersByUser(id));
    }

    @GetMapping("/{id}/orders-by-user")
    public ResponseEntity<UserWithOrdersResponseDTO> getUserWithOrders(@PathVariable Long id) {
        log.debug("Get user with orders for id={}", id);
        return ResponseEntity.ok(userService.getUserWithOrdersById(id));
    }
}
