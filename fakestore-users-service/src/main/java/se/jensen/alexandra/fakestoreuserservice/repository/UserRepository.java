package se.jensen.alexandra.fakestoreuserservice.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import se.jensen.alexandra.fakestoreuserservice.model.User;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}
