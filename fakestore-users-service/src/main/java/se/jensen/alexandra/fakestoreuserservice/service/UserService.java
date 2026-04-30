package se.jensen.alexandra.fakestoreuserservice.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import se.jensen.alexandra.fakestoreuserservice.dto.OrderResponseDTO;
import se.jensen.alexandra.fakestoreuserservice.dto.UserRequestDTO;
import se.jensen.alexandra.fakestoreuserservice.dto.UserResponseDTO;
import se.jensen.alexandra.fakestoreuserservice.dto.UserWithOrdersResponseDTO;
import se.jensen.alexandra.fakestoreuserservice.mapper.OrderMapper;
import se.jensen.alexandra.fakestoreuserservice.mapper.UserMapper;
import se.jensen.alexandra.fakestoreuserservice.model.Order;
import se.jensen.alexandra.fakestoreuserservice.model.User;
import se.jensen.alexandra.fakestoreuserservice.repository.OrderRepository;
import se.jensen.alexandra.fakestoreuserservice.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final UserMapper userMapper;
    private final OrderMapper orderMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, OrderRepository orderRepository, UserMapper userMapper, OrderMapper orderMapper, PasswordEncoder passwordEncoder) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.orderMapper = orderMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDTO addUser(UserRequestDTO dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("User with email " + dto.email() + " already exists.");
        }

        User user = userMapper.fromDto(dto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    public UserResponseDTO updateUser(UserRequestDTO dto, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        userMapper.fromDto(user, dto);

        if (dto.password() != null && !dto.password().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.password()));
        }
        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        return userMapper.toDto(user);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    public UserWithOrdersResponseDTO getUserWithOrdersById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        List<Order> orders = orderRepository.findByUser(user);

        List<OrderResponseDTO> orderDtos = orders.stream()
                .map(orderMapper::toDto)
                .toList();

        return new UserWithOrdersResponseDTO(
                userMapper.toDto(user),
                orderDtos
        );
    }

}
