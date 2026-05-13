package se.jensen.alexandra.fakestoreuserservice.dto.order;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(
        Long orderId,
        Long customerId,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime orderDate,
        String status,
        List<OrderItemResponseDTO> items,
        Double totalAmount
) {
}
