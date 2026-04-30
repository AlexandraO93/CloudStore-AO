package se.jensen.alexandra.fakestoreuserservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.jensen.alexandra.fakestoreuserservice.model.Order;
import se.jensen.alexandra.fakestoreuserservice.model.User;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);

}
