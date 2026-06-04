package se.jensen.alexandra.fakestoreproductservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.jensen.alexandra.fakestoreproductservice.model.Product;
import se.jensen.alexandra.fakestoreproductservice.service.ProductService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping("/fetch")
    public ResponseEntity<List<Product>> fetchProducts() {
        log.info("Fetching products from external API and saving to database");
        List<Product> products = service.fetchAndSaveProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        log.info("Retrieving all products from database");
        List<Product> products = service.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        log.info("Retrieving product with id={}", id);
        return ResponseEntity.ok(service.getProductById(id));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likeProduct(@PathVariable Long id, Principal principal) {
        log.info("User {} toggling like for product with id={}", principal.getName(), id);
        service.toggleLike(id, principal.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/liked")
    public ResponseEntity<List<Product>> getLikedProducts(@RequestParam String email) {
        log.info("Retrieving liked products for user with email={}", email);
        List<Product> likedProducts = service.findAllLikedByUser(email);
        return ResponseEntity.ok(likedProducts);
    }
}
