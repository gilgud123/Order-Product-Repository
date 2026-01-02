package com.katya.test.productrestassignement.repository;

import com.katya.test.productrestassignement.entity.Order;
import com.katya.test.productrestassignement.entity.Product;
import com.katya.test.productrestassignement.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create-drop"})
public class OrderRepositoryTest {

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
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    public void whenFindByUserId_thenReturnUserOrders() {
        // given
        User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setFirstName("Test");
        user1.setLastName("User");
        user1 = userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setFirstName("Test");
        user2.setLastName("User");
        user2 = userRepository.save(user2);

        Product product = new Product();
        product.setName("Laptop");
        product.setDescription("Test product");
        product.setPrice(new BigDecimal("999.99"));
        product.setStockQuantity(100);
        product.setCategory("Electronics");
        product = productRepository.save(product);

        Order order1 = new Order();
        order1.setUser(user1);
        order1.setProducts(new ArrayList<>());
        order1.getProducts().add(product);
        order1.setTotalAmount(new BigDecimal("999.99"));
        order1.setStatus(Order.OrderStatus.PENDING);
        orderRepository.save(order1);

        Order order2 = new Order();
        order2.setUser(user1);
        order2.setProducts(new ArrayList<>());
        order2.getProducts().add(product);
        order2.setTotalAmount(new BigDecimal("999.99"));
        order2.setStatus(Order.OrderStatus.SHIPPED);
        orderRepository.save(order2);

        Order order3 = new Order();
        order3.setUser(user2);
        order3.setProducts(new ArrayList<>());
        order3.getProducts().add(product);
        order3.setTotalAmount(new BigDecimal("999.99"));
        order3.setStatus(Order.OrderStatus.PENDING);
        orderRepository.save(order3);

        // when
        Page<Order> found = orderRepository.findByUserId(user1.getId(), PageRequest.of(0, 10));

        // then
        Long userId1 = user1.getId();
        assertThat(found.getContent()).hasSize(2);
        assertThat(found.getContent()).allMatch(order -> order.getUser().getId().equals(userId1));
    }

    @Test
    public void whenFindByStatus_thenReturnOrdersWithStatus() {
        // given
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user = userRepository.save(user);

        Product product = new Product();
        product.setName("Laptop");
        product.setDescription("Test product");
        product.setPrice(new BigDecimal("999.99"));
        product.setStockQuantity(100);
        product.setCategory("Electronics");
        product = productRepository.save(product);

        Order order1 = new Order();
        order1.setUser(user);
        order1.setProducts(new ArrayList<>());
        order1.getProducts().add(product);
        order1.setTotalAmount(new BigDecimal("999.99"));
        order1.setStatus(Order.OrderStatus.PENDING);
        orderRepository.save(order1);

        Order order2 = new Order();
        order2.setUser(user);
        order2.setProducts(new ArrayList<>());
        order2.getProducts().add(product);
        order2.setTotalAmount(new BigDecimal("999.99"));
        order2.setStatus(Order.OrderStatus.PENDING);
        orderRepository.save(order2);

        Order order3 = new Order();
        order3.setUser(user);
        order3.setProducts(new ArrayList<>());
        order3.getProducts().add(product);
        order3.setTotalAmount(new BigDecimal("999.99"));
        order3.setStatus(Order.OrderStatus.SHIPPED);
        orderRepository.save(order3);

        // when
        Page<Order> found = orderRepository.findByStatus(Order.OrderStatus.PENDING, PageRequest.of(0, 10));

        // then
        assertThat(found.getContent()).hasSize(2);
        assertThat(found.getContent()).allMatch(order -> order.getStatus() == Order.OrderStatus.PENDING);
    }

    @Test
    public void whenFindByFilters_withUserId_thenReturnFilteredOrders() {
        // given
        User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setFirstName("Test");
        user1.setLastName("User");
        user1 = userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setFirstName("Test");
        user2.setLastName("User");
        user2 = userRepository.save(user2);

        Product product = new Product();
        product.setName("Laptop");
        product.setDescription("Test product");
        product.setPrice(new BigDecimal("999.99"));
        product.setStockQuantity(100);
        product.setCategory("Electronics");
        product = productRepository.save(product);

        Order order1 = new Order();
        order1.setUser(user1);
        order1.setProducts(new ArrayList<>());
        order1.getProducts().add(product);
        order1.setTotalAmount(new BigDecimal("999.99"));
        order1.setStatus(Order.OrderStatus.PENDING);
        orderRepository.save(order1);

        Order order2 = new Order();
        order2.setUser(user2);
        order2.setProducts(new ArrayList<>());
        order2.getProducts().add(product);
        order2.setTotalAmount(new BigDecimal("999.99"));
        order2.setStatus(Order.OrderStatus.PENDING);
        orderRepository.save(order2);

        // when
        Page<Order> found = orderRepository.findByFilters(user1.getId(), null, PageRequest.of(0, 10));

        // then
        assertThat(found.getContent()).hasSize(1);
        assertThat(found.getContent().get(0).getUser().getId()).isEqualTo(user1.getId());
    }

    @Test
    public void whenFindByFilters_withStatus_thenReturnFilteredOrders() {
        // given
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user = userRepository.save(user);

        Product product = new Product();
        product.setName("Laptop");
        product.setDescription("Test product");
        product.setPrice(new BigDecimal("999.99"));
        product.setStockQuantity(100);
        product.setCategory("Electronics");
        product = productRepository.save(product);

        Order order1 = new Order();
        order1.setUser(user);
        order1.setProducts(new ArrayList<>());
        order1.getProducts().add(product);
        order1.setTotalAmount(new BigDecimal("999.99"));
        order1.setStatus(Order.OrderStatus.PENDING);
        orderRepository.save(order1);

        Order order2 = new Order();
        order2.setUser(user);
        order2.setProducts(new ArrayList<>());
        order2.getProducts().add(product);
        order2.setTotalAmount(new BigDecimal("999.99"));
        order2.setStatus(Order.OrderStatus.SHIPPED);
        orderRepository.save(order2);

        // when
        Page<Order> found = orderRepository.findByFilters(null, Order.OrderStatus.SHIPPED, PageRequest.of(0, 10));

        // then
        assertThat(found.getContent()).hasSize(1);
        assertThat(found.getContent().get(0).getStatus()).isEqualTo(Order.OrderStatus.SHIPPED);
    }

    @Test
    public void whenFindByFilters_withUserIdAndStatus_thenReturnFilteredOrders() {
        // given
        User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setFirstName("Test");
        user1.setLastName("User");
        user1 = userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setFirstName("Test");
        user2.setLastName("User");
        user2 = userRepository.save(user2);

        Product product = new Product();
        product.setName("Laptop");
        product.setDescription("Test product");
        product.setPrice(new BigDecimal("999.99"));
        product.setStockQuantity(100);
        product.setCategory("Electronics");
        product = productRepository.save(product);

        Order order1 = new Order();
        order1.setUser(user1);
        order1.setProducts(new ArrayList<>());
        order1.getProducts().add(product);
        order1.setTotalAmount(new BigDecimal("999.99"));
        order1.setStatus(Order.OrderStatus.PENDING);
        orderRepository.save(order1);

        Order order2 = new Order();
        order2.setUser(user1);
        order2.setProducts(new ArrayList<>());
        order2.getProducts().add(product);
        order2.setTotalAmount(new BigDecimal("999.99"));
        order2.setStatus(Order.OrderStatus.SHIPPED);
        orderRepository.save(order2);

        Order order3 = new Order();
        order3.setUser(user2);
        order3.setProducts(new ArrayList<>());
        order3.getProducts().add(product);
        order3.setTotalAmount(new BigDecimal("999.99"));
        order3.setStatus(Order.OrderStatus.PENDING);
        orderRepository.save(order3);

        // when
        Page<Order> found = orderRepository.findByFilters(user1.getId(), Order.OrderStatus.PENDING, PageRequest.of(0, 10));

        // then
        assertThat(found.getContent()).hasSize(1);
        assertThat(found.getContent().get(0).getUser().getId()).isEqualTo(user1.getId());
        assertThat(found.getContent().get(0).getStatus()).isEqualTo(Order.OrderStatus.PENDING);
    }

    @Test
    public void whenFindByFilters_withNoFilters_thenReturnAllOrders() {
        // given
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user = userRepository.save(user);

        Product product = new Product();
        product.setName("Laptop");
        product.setDescription("Test product");
        product.setPrice(new BigDecimal("999.99"));
        product.setStockQuantity(100);
        product.setCategory("Electronics");
        product = productRepository.save(product);

        Order order1 = new Order();
        order1.setUser(user);
        order1.setProducts(new ArrayList<>());
        order1.getProducts().add(product);
        order1.setTotalAmount(new BigDecimal("999.99"));
        order1.setStatus(Order.OrderStatus.PENDING);
        orderRepository.save(order1);

        Order order2 = new Order();
        order2.setUser(user);
        order2.setProducts(new ArrayList<>());
        order2.getProducts().add(product);
        order2.setTotalAmount(new BigDecimal("999.99"));
        order2.setStatus(Order.OrderStatus.SHIPPED);
        orderRepository.save(order2);

        // when
        Page<Order> found = orderRepository.findByFilters(null, null, PageRequest.of(0, 10));

        // then
        assertThat(found.getContent()).hasSize(2);
    }

    @Test
    public void whenSaveOrder_withMultipleProducts_thenOrderIsSaved() {
        // given
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user = userRepository.save(user);

        Product product1 = new Product();
        product1.setName("Laptop");
        product1.setDescription("Test product");
        product1.setPrice(new BigDecimal("999.99"));
        product1.setStockQuantity(100);
        product1.setCategory("Electronics");
        product1 = productRepository.save(product1);

        Product product2 = new Product();
        product2.setName("Mouse");
        product2.setDescription("Test product");
        product2.setPrice(new BigDecimal("29.99"));
        product2.setStockQuantity(100);
        product2.setCategory("Electronics");
        product2 = productRepository.save(product2);

        Order order = new Order();
        order.setUser(user);
        order.setProducts(new ArrayList<>());
        order.getProducts().add(product1);
        order.getProducts().add(product2);
        order.setTotalAmount(new BigDecimal("1029.98"));
        order.setStatus(Order.OrderStatus.PENDING);

        // when
        Order savedOrder = orderRepository.save(order);

        // then
        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getProducts()).hasSize(2);
        assertThat(savedOrder.getTotalAmount()).isEqualByComparingTo(new BigDecimal("1029.98"));
    }
}

