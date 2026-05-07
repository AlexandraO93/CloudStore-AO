package se.jensen.alexandra.fakestoreuserservice.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.jensen.alexandra.fakestoreuserservice.dto.*;
import se.jensen.alexandra.fakestoreuserservice.service.OrderService;
import se.jensen.alexandra.fakestoreuserservice.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final OrderService orderService;

    public UserController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> addUser
            (@Valid @RequestBody UserRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.addUser(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser
            (@PathVariable Long id,
             @Valid @RequestBody UserRequestDTO request) {
        return ResponseEntity.ok().body(userService.updateUser(request, id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById
            (@PathVariable Long id) {
        return ResponseEntity.ok().body(userService.getUserById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser
            (@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/orders")
    public ResponseEntity<OrderResponseDTO> createOrderForUser(
            @PathVariable Long id,
            @Valid @RequestBody OrderRequestDTO orderRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(id, orderRequestDTO));
    }

    @GetMapping("/{id}/orders")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrdersByUser(
            @PathVariable Long id) {
        return ResponseEntity.ok().body(orderService.getAllOrdersByUser(id));
    }

    @GetMapping("/{id}/orders-by-user")
    public ResponseEntity<UserWithOrdersResponseDTO> getUserWithOrders(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserWithOrdersById(id));
    }
}
