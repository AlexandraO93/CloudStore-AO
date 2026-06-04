package se.jensen.alexandra.fakestoreproductservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import se.jensen.alexandra.fakestoreproductservice.model.Product;
import se.jensen.alexandra.fakestoreproductservice.repository.ProductRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository repository;
    private final RestClient restClient;

    @Value("${fakestore.api.url}")
    String url;

    public ProductService(ProductRepository repository, RestClient restClient) {
        this.repository = repository;
        this.restClient = restClient;
    }

    @Transactional
    public List<Product> fetchAndSaveProducts() {
        log.debug("Fetching products from external API at {}", url);
        Product[] response = restClient.get()
                .uri(url)
                .retrieve()
                .body(Product[].class);

        if (response != null) {
            for (Product incomingProduct : response) {
                List<Product> existingProducts = repository.findByTitle(incomingProduct.getTitle());

                if (!existingProducts.isEmpty()) {
                    log.debug("Product with title '{}' already exists. Updating existing product with new data.", incomingProduct.getTitle());
                    Product existingProduct = existingProducts.get(0);
                    existingProduct.setPrice(incomingProduct.getPrice());
                    existingProduct.setDescription(incomingProduct.getDescription());
                    existingProduct.setCategory(incomingProduct.getCategory());
                    existingProduct.setImage(incomingProduct.getImage());
                    repository.save(existingProduct);
                } else {
                    incomingProduct.setId(null);
                    repository.save(incomingProduct);
                }
            }
        }
        return repository.findAll();
    }

    public List<Product> getAllProducts() {
        log.info("Retrieving all products from database");
        return repository.findAll();
    }

    public Product getProductById(Long id) {
        log.info("Retrieving product with id={}", id);
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
    }

    public void toggleLike(Long id, String email) {
        log.debug("Toggling like for product with id={} by user with email={}", id, email);
        Product product = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Produkten hittades inte"));

        if (product.getLikedByEmails().contains(email)) {
            product.getLikedByEmails().remove(email);
        } else {
            product.getLikedByEmails().add(email);
        }

        repository.save(product);
    }

    public List<Product> findAllLikedByUser(String email) {
        log.info("Finding all products liked by user with email={}", email);
        return repository.findAllByLikedByEmailsContaining(email);
    }
}
