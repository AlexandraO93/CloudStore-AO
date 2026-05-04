package se.jensen.alexandra.fakestoreproductservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClient;
import se.jensen.alexandra.fakestoreproductservice.model.Product;
import se.jensen.alexandra.fakestoreproductservice.repository.ProductRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ProductServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository repository;

    @Autowired
    private ProductService service;

    private RestClient restClientMock;
    private RestClient.RequestHeadersUriSpec uriSpec;
    private RestClient.RequestHeadersSpec headersSpec;
    private RestClient.ResponseSpec responseSpec;

    @BeforeEach
    public void setUp() {
        repository.deleteAll();

        // Initiera alla mockar
        restClientMock = mock(RestClient.class);
        uriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        headersSpec = mock(RestClient.RequestHeadersSpec.class);
        responseSpec = mock(RestClient.ResponseSpec.class);

        // Bygger kedjan: restClient.get().uri().retrieve().body()
        when(restClientMock.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);

        // Stoppar in fake RestTemplate i service
        ReflectionTestUtils.setField(service, "restClient", restClientMock);
    }

    // 1 Hämta från API och spara i H2
    @Test
    public void fetchAndSaveProducts_shouldSaveAndReturnProducts() {

        // Arrange
        Product p1 = new Product();
        p1.setTitle("Product One");

        when(responseSpec.body(Product[].class))
                .thenReturn(new Product[]{p1});

        // Act
        List<Product> result = service.fetchAndSaveProducts();

        // Assert
        assertEquals(1, repository.count());
        Product savedProduct = result.get(0);
        assertEquals("Product One", savedProduct.getTitle());

        // Kontrollerar att produkten sparades i H2-databasen
        assertTrue(repository.findById(savedProduct.getId()).isPresent());
        // Verifiera att retrieve() anropades
        verify(headersSpec).retrieve();
    }

    // 2 API returnerar null
    @Test
    public void fetchAndSaveProducts_shouldReturnEmptyList_whenApiReturnsNull() {

        // Arrange
        when(responseSpec.body(Product[].class))
                .thenReturn(null);

        // Act & Assert
        List<Product> result = service.fetchAndSaveProducts();

        //Kontrollerar att inga produkter sparades i H2-databasen
        assertNotNull(result);
        assertEquals(0, repository.count());
    }


    // 3 Hämta produkt som finns
    @Test
    public void getProductById_shouldReturnProduct_whenFound() {

        // Arrange
        Product p = new Product();
        p.setTitle("Test product");
        Product saved = repository.save(p);

        // Act
        Product result = service.getProductById(saved.getId());

        // Assert
        // Kontrollerar att rätt produkt returnerades med titel och id
        assertNotNull(result);
        assertEquals(saved.getId(), result.getId());
        assertEquals("Test product", result.getTitle());
    }

    // 4 Produkt finns inte
    @Test
    public void getProductById_shouldThrowException_whenNotFound() {

        // Arrange

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            service.getProductById(999L);
        });
    }

    // 5 Hämta alla produkter
    @Test
    public void getAllProducts_shouldReturnAllProducts() {

        // Arrange
        Product p1 = new Product();
        p1.setTitle("P1");
        Product p2 = new Product();
        p2.setTitle("P2");

        repository.save(p1);
        repository.save(p2);

        // Act
        List<Product> result = service.getAllProducts();

        // Assert
        assertEquals(2, result.size());
    }

    // 6 API returnerar tom lista
    @Test
    public void fetchAndSaveProducts_shouldReturnEmptyList_whenApiReturnsEmpty() {
        // Arrange
        when(responseSpec.body(Product[].class))
                .thenReturn(new Product[]{});

        // Act
        List<Product> result = service.fetchAndSaveProducts();

        // Assert
        assertEquals(0, result.size());
        assertEquals(0, repository.count());

        // Verifiera att retrieve() anropades
        verify(headersSpec).retrieve();
    }

    // 7 Exception vid misslyckat API-anrop
    @Test
    public void fetchAndSaveProducts_shouldThrowException_whenApiFails() {

        when(responseSpec.body(Product[].class))
                .thenThrow(new RuntimeException("API error"));

        assertThrows(RuntimeException.class, () -> {
            service.fetchAndSaveProducts();
        });

        assertEquals(0, repository.count());
    }

    // 8 Verifierar att man inte kan hämta produkter utan token
    @Test
    public void getAllProducts_withoutToken_shouldReturn401() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/products"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isUnauthorized());
    }

    // 9 Verifierar att man KAN hämta produkter med en giltig JWT-token
    @Test
    public void getAllProducts_withValidJwt_shouldReturnProducts() throws Exception {
        // Arrange: Spara en produkt i H2
        Product p = new Product();
        p.setTitle("Secure Product");
        repository.save(p);

        // Act & Assert: Anropa endpointen med en simulerad JWT
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/products")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_read")))) // Simulerar VG-kravet på JWT-säkerhet
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[0].title").value("Secure Product"));
    }
}
