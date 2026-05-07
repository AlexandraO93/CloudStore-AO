package se.jensen.alexandra.fakestoreproductservice.service;

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
        Product[] response = restClient.get()
                .uri(url)
                .retrieve()
                .body(Product[].class);

        if (response != null) {
            for (Product incomingProduct : response) {
                repository.findByTitle(incomingProduct.getTitle())
                        .ifPresentOrElse(
                                existingProduct -> {
                                    existingProduct.setPrice(incomingProduct.getPrice());
                                    existingProduct.setDescription(incomingProduct.getDescription());
                                    existingProduct.setCategory(incomingProduct.getCategory());
                                    existingProduct.setImage(incomingProduct.getImage());
                                    repository.save(existingProduct);
                                },
                                () -> {
                                    incomingProduct.setId(null);
                                    repository.save(incomingProduct);
                                }
                        );
            }
        }
        return repository.findAll();
    }

    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    public Product getProductById(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
    }

    public void toggleLike(Long id, String email) {
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

        return repository.findAllByLikedByEmailsContaining(email);
    }
}
