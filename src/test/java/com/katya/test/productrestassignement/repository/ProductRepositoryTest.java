package com.katya.test.productrestassignement.repository;

import com.katya.test.productrestassignement.entity.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create-drop"})
public class ProductRepositoryTest {

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
    private ProductRepository productRepository;

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
    }

    @Test
    public void whenFindByCategory_thenReturnProducts() {
        // given
        Product product = new Product();
        product.setName("Test Product");
        product.setCategory("Test Category");
        product.setPrice(BigDecimal.TEN);
        product.setStockQuantity(100);
        productRepository.save(product);

        // when
        Page<Product> found = productRepository.findByCategory("Test Category", PageRequest.of(0, 10));

        // then
        assertThat(found.getContent()).hasSize(1);
        assertThat(found.getContent().get(0).getCategory()).isEqualTo("Test Category");
    }

    @Test
    public void whenFindByPriceBetween_thenReturnProducts() {
        // given
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(new BigDecimal("15.00"));
        product.setStockQuantity(50);
        productRepository.save(product);

        // when
        Page<Product> found = productRepository.findByPriceBetween(new BigDecimal("10.00"), new BigDecimal("20.00"), PageRequest.of(0, 10));

        // then
        assertThat(found.getContent()).hasSize(1);
        assertThat(found.getContent().get(0).getPrice()).isBetween(new BigDecimal("10.00"), new BigDecimal("20.00"));
    }

    @Test
    public void whenSearchProducts_thenReturnMatchingProducts() {
        // given
        Product product = new Product();
        product.setName("UniqueProductName");
        product.setDescription("Some description");
        product.setCategory("Some category");
        product.setPrice(new BigDecimal("99.99"));
        product.setStockQuantity(10);
        productRepository.save(product);

        // when
        Page<Product> found = productRepository.searchProducts("UniqueProductName", PageRequest.of(0, 10));

        // then
        assertThat(found.getContent()).hasSize(1);
        assertThat(found.getContent().get(0).getName()).isEqualTo("UniqueProductName");
    }

    @Test
    public void whenFindByFilters_thenReturnFilteredProducts() {
        // given
        Product product = new Product();
        product.setName("Filtered Product");
        product.setCategory("FilterCategory");
        product.setPrice(new BigDecimal("25.00"));
        product.setStockQuantity(20);
        productRepository.save(product);

        // when
        Page<Product> found = productRepository.findByFilters("FilterCategory", new BigDecimal("20.00"), new BigDecimal("30.00"), PageRequest.of(0, 10));

        // then
        assertThat(found.getContent()).hasSize(1);
        assertThat(found.getContent().get(0).getCategory()).isEqualTo("FilterCategory");
    }
}
