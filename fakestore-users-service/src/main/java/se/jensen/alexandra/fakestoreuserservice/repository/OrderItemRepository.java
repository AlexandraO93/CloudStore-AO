package se.jensen.alexandra.fakestoreuserservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.jensen.alexandra.fakestoreuserservice.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
