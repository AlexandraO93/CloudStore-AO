package se.jensen.alexandra.fakestoreuserservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import se.jensen.alexandra.fakestoreuserservice.dto.order.OrderRequestDTO;
import se.jensen.alexandra.fakestoreuserservice.dto.order.OrderResponseDTO;
import se.jensen.alexandra.fakestoreuserservice.dto.order.ProductDTO;
import se.jensen.alexandra.fakestoreuserservice.mapper.OrderMapper;
import se.jensen.alexandra.fakestoreuserservice.model.Order;
import se.jensen.alexandra.fakestoreuserservice.model.OrderItem;
import se.jensen.alexandra.fakestoreuserservice.model.User;
import se.jensen.alexandra.fakestoreuserservice.repository.OrderRepository;
import se.jensen.alexandra.fakestoreuserservice.repository.UserRepository;

import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final OrderMapper mapper;

    @Value("${product.service.url}")
    private String productUrl;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, RestTemplate restTemplate, OrderMapper mapper) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
        this.mapper = mapper;
    }

    public OrderResponseDTO createOrder(Long userId, OrderRequestDTO requestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Order order = new Order();
        order.setUser(user);

        List<OrderItem> items = requestDTO.items().stream().map(itemDTO -> {
            String productServiceURL = productUrl + itemDTO.productId();

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            String token = (attributes != null) ? attributes.getRequest().getHeader("Authorization") : null;

            if (token == null || token.isEmpty()) {
                throw new IllegalStateException("No authorization token found in request");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            try {
                ResponseEntity<ProductDTO> response = restTemplate.exchange(
                        productServiceURL,
                        HttpMethod.GET,
                        entity,
                        ProductDTO.class
                );

                ProductDTO product = response.getBody();

                if (product == null) {
                    throw new IllegalArgumentException("Product not found with id: " + itemDTO.productId());
                }

                OrderItem item = new OrderItem();
                item.setProductId(itemDTO.productId());
                item.setQuantity(itemDTO.quantity());
                item.setOrder(order);
                item.setPrice(product.price());
                return item;
            } catch (HttpClientErrorException.NotFound e) {
                throw new IllegalArgumentException("Produkten med ID " + itemDTO.productId() + " existerar inte i produkttjänsten.");
            }
        }).toList();

        order.setOrderItems(items);
        order.setStatus(Order.Status.CREATED);

        Order savedOrder = orderRepository.save(order);
        return mapper.toDto(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getAllOrdersByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        List<Order> orders = orderRepository.findByUser(user);
        return orders.stream().map(mapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));
        return mapper.toDto(order);
    }

    @Transactional
    public OrderResponseDTO updateOrderStatus(Long orderId, Order.Status newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));

        if (order.getStatus() == Order.Status.CANCELLED) {
            throw new IllegalStateException("Cannot update status of a cancelled order.");
        }

        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        return mapper.toDto(updatedOrder);
    }

    @Transactional
    public OrderResponseDTO cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));

        if (order.getStatus() == Order.Status.CANCELLED) {
            throw new IllegalStateException("Order is already cancelled.");
        }

        order.setStatus(Order.Status.CANCELLED);
        orderRepository.save(order);
        return mapper.toDto(order);
    }
}
