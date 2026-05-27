package se.jensen.alexandra.fakestoreproductservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.jensen.alexandra.fakestoreproductservice.model.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByLikedByEmailsContaining(String email);

    List<Product> findByTitle(String title);
}
