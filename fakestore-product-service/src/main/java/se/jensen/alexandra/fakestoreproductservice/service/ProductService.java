package se.jensen.alexandra.fakestoreproductservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import se.jensen.alexandra.fakestoreproductservice.model.Product;
import se.jensen.alexandra.fakestoreproductservice.repository.ProductRepository;

import java.util.Arrays;
import java.util.List;

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
            List<Product> products = Arrays.asList(response);
            // NOLLSTÄLL ID:n för att undvika Optimistic Locking/Merge-problem - uppstod flera gånger
            products.forEach(product -> product.setId(null));

            repository.saveAll(products);
        }
        return repository.findAll();
    }

    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    public Product getProductById(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
    }
}
