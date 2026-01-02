package com.katya.test.productrestassignement.config;

import com.katya.test.productrestassignement.entity.Order;
import com.katya.test.productrestassignement.entity.Product;
import com.katya.test.productrestassignement.entity.User;
import com.katya.test.productrestassignement.repository.OrderRepository;
import com.katya.test.productrestassignement.repository.ProductRepository;
import com.katya.test.productrestassignement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Data initialization for development/testing purposes
 * Activate with spring.profiles.active=dev
 */
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    @Bean
    @Profile("dev")
    CommandLineRunner initDatabase(UserRepository userRepository,
                                   ProductRepository productRepository,
                                   OrderRepository orderRepository) {
        return args -> {
            // Check if data already exists
            if (userRepository.count() > 0) {
                return;
            }

            // Create sample users
            User user1 = new User();
            user1.setUsername("johndoe");
            user1.setEmail("john.doe@example.com");
            user1.setFirstName("John");
            user1.setLastName("Doe");

            User user2 = new User();
            user2.setUsername("janedoe");
            user2.setEmail("jane.doe@example.com");
            user2.setFirstName("Jane");
            user2.setLastName("Doe");

            userRepository.saveAll(Arrays.asList(user1, user2));

            // Create sample products
            Product product1 = new Product();
            product1.setName("Laptop");
            product1.setDescription("High-performance laptop");
            product1.setPrice(new BigDecimal("999.99"));
            product1.setStockQuantity(50);
            product1.setCategory("Electronics");

            Product product2 = new Product();
            product2.setName("Mouse");
            product2.setDescription("Wireless mouse");
            product2.setPrice(new BigDecimal("29.99"));
            product2.setStockQuantity(200);
            product2.setCategory("Electronics");

            Product product3 = new Product();
            product3.setName("Desk Chair");
            product3.setDescription("Ergonomic office chair");
            product3.setPrice(new BigDecimal("249.99"));
            product3.setStockQuantity(30);
            product3.setCategory("Furniture");

            productRepository.saveAll(Arrays.asList(product1, product2, product3));

            // Create sample order
            Order order1 = new Order();
            order1.setUser(user1);
            order1.setProducts(Arrays.asList(product1, product2));
            order1.setTotalAmount(product1.getPrice().add(product2.getPrice()));
            order1.setStatus(Order.OrderStatus.PENDING);

            orderRepository.save(order1);

            System.out.println("Sample data initialized successfully!");
        };
    }
}

