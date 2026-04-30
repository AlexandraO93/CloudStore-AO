package se.jensen.alexandra.fakestoreuserservice.mapper;

import org.springframework.stereotype.Component;
import se.jensen.alexandra.fakestoreuserservice.dto.OrderItemResponseDTO;
import se.jensen.alexandra.fakestoreuserservice.dto.OrderResponseDTO;
import se.jensen.alexandra.fakestoreuserservice.model.Order;
import se.jensen.alexandra.fakestoreuserservice.model.OrderItem;

import java.util.List;

@Component
public class OrderMapper {

    public OrderResponseDTO toDto(Order order) {
        if (order == null) {
            return null;
        }

        List<OrderItemResponseDTO> itemDtos = order.getOrderItems().stream()
                .map(this::toItemDto)
                .toList();

        double total = order.getOrderItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        return new OrderResponseDTO(
                order.getOrderId(),
                order.getUser().getId(),
                order.getOrderDate(),
                order.getStatus().name(),
                itemDtos,
                total
        );
    }

    private OrderItemResponseDTO toItemDto(OrderItem item) {
        return new OrderItemResponseDTO(
                item.getId(),
                item.getProductId(),
                item.getQuantity(),
                item.getPrice()
        );
    }
}
