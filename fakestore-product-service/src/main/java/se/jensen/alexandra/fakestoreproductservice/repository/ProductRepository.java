package se.jensen.alexandra.fakestoreproductservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.jensen.alexandra.fakestoreproductservice.model.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByLikedByEmailsContaining(String email);

    Optional<Product> findByTitle(String title);

    //Lägg eventuellt till för filtrering av kategori/sökord??
}
