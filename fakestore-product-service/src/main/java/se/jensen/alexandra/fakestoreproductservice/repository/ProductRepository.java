package se.jensen.alexandra.fakestoreproductservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.jensen.alexandra.fakestoreproductservice.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    //Lägg eventuellt till för filtrering av kategori/sökord??
}
