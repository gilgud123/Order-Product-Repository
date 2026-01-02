package com.katya.test.productrestassignement.service;

import com.katya.test.productrestassignement.dto.OrderDTO;
import com.katya.test.productrestassignement.entity.Order;
import com.katya.test.productrestassignement.entity.Product;
import com.katya.test.productrestassignement.entity.User;
import com.katya.test.productrestassignement.repository.OrderRepository;
import com.katya.test.productrestassignement.repository.ProductRepository;
import com.katya.test.productrestassignement.repository.UserRepository;
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
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create-drop"})
public class OrderServiceTest {

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
    private OrderService orderService;

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
    public void whenGetAllOrders_thenReturnOrderPage() {
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

        Order order = new Order();
        order.setUser(user);
        order.setProducts(new ArrayList<>());
        order.getProducts().add(product);
        order.setTotalAmount(new BigDecimal("999.99"));
        order.setStatus(Order.OrderStatus.PENDING);
        orderRepository.save(order);

        // when
        Page<OrderDTO> result = orderService.getAllOrders(PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUserId()).isEqualTo(user.getId());
    }

    @Test
    public void whenGetOrderById_thenReturnOrder() {
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

        Order order = new Order();
        order.setUser(user);
        order.setProducts(new ArrayList<>());
        order.getProducts().add(product);
        order.setTotalAmount(new BigDecimal("999.99"));
        order.setStatus(Order.OrderStatus.PENDING);
        order = orderRepository.save(order);

        // when
        OrderDTO result = orderService.getOrderById(order.getId());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(user.getId());
        assertThat(result.getStatus()).isEqualTo(Order.OrderStatus.PENDING);
    }

    @Test
    public void whenGetOrdersByUserId_thenReturnUserOrders() {
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
        Page<OrderDTO> result = orderService.getOrdersByUserId(user.getId(), PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).hasSize(2);
        Long userId = user.getId();
        assertThat(result.getContent()).allMatch(orderDTO -> orderDTO.getUserId().equals(userId));
    }

    @Test
    public void whenCreateOrder_thenReturnSavedOrder() {
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

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setUserId(user.getId());
        orderDTO.setProductIds(Arrays.asList(product1.getId(), product2.getId()));

        // when
        OrderDTO result = orderService.createOrder(orderDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getUserId()).isEqualTo(user.getId());
        assertThat(result.getStatus()).isEqualTo(Order.OrderStatus.PENDING);
        assertThat(result.getTotalAmount()).isEqualByComparingTo(new BigDecimal("1029.98"));
        assertThat(result.getProductIds()).hasSize(2);
        assertThat(result.getProductIds()).containsExactlyInAnyOrder(product1.getId(), product2.getId());
    }

    @Test
    public void whenUpdateOrderStatus_thenReturnUpdatedOrder() {
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

        Order order = new Order();
        order.setUser(user);
        order.setProducts(new ArrayList<>());
        order.getProducts().add(product);
        order.setTotalAmount(new BigDecimal("999.99"));
        order.setStatus(Order.OrderStatus.PENDING);
        order = orderRepository.save(order);

        // when
        OrderDTO result = orderService.updateOrderStatus(order.getId(), Order.OrderStatus.SHIPPED);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(Order.OrderStatus.SHIPPED);

        Order updatedOrder = orderRepository.findById(order.getId()).orElse(null);
        assertThat(updatedOrder).isNotNull();
        assertThat(updatedOrder.getStatus()).isEqualTo(Order.OrderStatus.SHIPPED);
    }

    @Test
    public void whenDeleteOrder_thenOrderIsDeleted() {
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

        Order order = new Order();
        order.setUser(user);
        order.setProducts(new ArrayList<>());
        order.getProducts().add(product);
        order.setTotalAmount(new BigDecimal("999.99"));
        order.setStatus(Order.OrderStatus.PENDING);
        order = orderRepository.save(order);

        Long orderId = order.getId();

        // when
        orderService.deleteOrder(orderId);

        // then
        assertThat(orderRepository.existsById(orderId)).isFalse();
    }
}

