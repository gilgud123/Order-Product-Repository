package com.katya.test.productrestassignement.service;

import com.katya.test.productrestassignement.dto.ProductDTO;
import com.katya.test.productrestassignement.entity.Product;
import com.katya.test.productrestassignement.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class ProductServiceTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("productdb")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
    }

    @Test
    public void whenGetAllProducts_thenReturnProductPage() {
        // given
        Product product = new Product();
        product.setName("Test Product");
        productRepository.save(product);

        // when
        Page<ProductDTO> result = productService.getAllProducts(PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Test Product");
    }

    @Test
    public void whenGetProductById_thenReturnProduct() {
        // given
        Product product = new Product();
        product.setName("Test Product");
        Product savedProduct = productRepository.save(product);

        // when
        ProductDTO result = productService.getProductById(savedProduct.getId());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Product");
    }

    @Test
    public void whenCreateProduct_thenReturnSavedProduct() {
        // given
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("New Product");

        // when
        ProductDTO result = productService.createProduct(productDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("New Product");

        Product savedProduct = productRepository.findById(result.getId()).orElse(null);
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo("New Product");
    }
}

