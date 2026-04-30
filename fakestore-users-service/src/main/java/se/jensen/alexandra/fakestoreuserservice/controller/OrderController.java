package se.jensen.alexandra.fakestoreuserservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.jensen.alexandra.fakestoreuserservice.dto.OrderResponseDTO;
import se.jensen.alexandra.fakestoreuserservice.model.Order;
import se.jensen.alexandra.fakestoreuserservice.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    //createOrder ligger under UserController eftersom det är en order som skapas av en user, och då är det mer logiskt att ha den där. Det är också där vi har access till userId som behövs för att skapa en order.

    //getAllOrdersByUser ligger också under UserController av samma anledning, det är mer logiskt att hämta orders som tillhör en user i UserController.

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrderById
            (@PathVariable Long orderId) {
        return ResponseEntity.ok(service.getOrderById(orderId));
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus
            (@PathVariable Long orderId,
             @RequestParam Order.Status newStatus) {
        return ResponseEntity.ok(service.updateOrderStatus(orderId, newStatus));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder
            (@PathVariable Long orderId) {
        service.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }

}
