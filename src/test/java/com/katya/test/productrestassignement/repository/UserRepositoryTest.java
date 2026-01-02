package com.katya.test.productrestassignement.repository;

import com.katya.test.productrestassignement.entity.User;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create-drop"})
public class UserRepositoryTest {

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
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    public void whenFindByUsername_thenReturnUser() {
        // given
        User user = new User();
        user.setUsername("johndoe");
        user.setEmail("john.doe@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        userRepository.save(user);

        // when
        Optional<User> found = userRepository.findByUsername("johndoe");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("johndoe");
        assertThat(found.get().getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    public void whenFindByUsername_withNonExistentUser_thenReturnEmpty() {
        // when
        Optional<User> found = userRepository.findByUsername("nonexistent");

        // then
        assertThat(found).isEmpty();
    }

    @Test
    public void whenFindByEmail_thenReturnUser() {
        // given
        User user = new User();
        user.setUsername("janedoe");
        user.setEmail("jane.doe@example.com");
        user.setFirstName("Jane");
        user.setLastName("Doe");
        userRepository.save(user);

        // when
        Optional<User> found = userRepository.findByEmail("jane.doe@example.com");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("jane.doe@example.com");
        assertThat(found.get().getUsername()).isEqualTo("janedoe");
    }

    @Test
    public void whenExistsByUsername_withExistingUser_thenReturnTrue() {
        // given
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        userRepository.save(user);

        // when
        boolean exists = userRepository.existsByUsername("testuser");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    public void whenExistsByUsername_withNonExistentUser_thenReturnFalse() {
        // when
        boolean exists = userRepository.existsByUsername("nonexistent");

        // then
        assertThat(exists).isFalse();
    }

    @Test
    public void whenExistsByEmail_withExistingEmail_thenReturnTrue() {
        // given
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        userRepository.save(user);

        // when
        boolean exists = userRepository.existsByEmail("test@example.com");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    public void whenExistsByEmail_withNonExistentEmail_thenReturnFalse() {
        // when
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        // then
        assertThat(exists).isFalse();
    }

    @Test
    public void whenSearchUsers_byUsername_thenReturnMatchingUsers() {
        // given
        User user1 = new User();
        user1.setUsername("johndoe");
        user1.setEmail("john@example.com");
        user1.setFirstName("John");
        user1.setLastName("Doe");
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("janedoe");
        user2.setEmail("jane@example.com");
        user2.setFirstName("Jane");
        user2.setLastName("Doe");
        userRepository.save(user2);

        User user3 = new User();
        user3.setUsername("bobsmith");
        user3.setEmail("bob@example.com");
        user3.setFirstName("Bob");
        user3.setLastName("Smith");
        userRepository.save(user3);

        // when
        Page<User> found = userRepository.searchUsers("john", PageRequest.of(0, 10));

        // then
        assertThat(found.getContent()).hasSize(1);
        assertThat(found.getContent().get(0).getUsername()).isEqualTo("johndoe");
    }

    @Test
    public void whenSearchUsers_byLastName_thenReturnMatchingUsers() {
        // given
        User user1 = new User();
        user1.setUsername("johndoe");
        user1.setEmail("john@example.com");
        user1.setFirstName("John");
        user1.setLastName("Doe");
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("janedoe");
        user2.setEmail("jane@example.com");
        user2.setFirstName("Jane");
        user2.setLastName("Doe");
        userRepository.save(user2);

        User user3 = new User();
        user3.setUsername("bobsmith");
        user3.setEmail("bob@example.com");
        user3.setFirstName("Bob");
        user3.setLastName("Smith");
        userRepository.save(user3);

        // when
        Page<User> found = userRepository.searchUsers("Doe", PageRequest.of(0, 10));

        // then
        assertThat(found.getContent()).hasSize(2);
        assertThat(found.getContent()).extracting(User::getLastName).containsOnly("Doe");
    }

    @Test
    public void whenSearchUsers_byEmail_thenReturnMatchingUsers() {
        // given
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("unique@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        userRepository.save(user);

        // when
        Page<User> found = userRepository.searchUsers("unique", PageRequest.of(0, 10));

        // then
        assertThat(found.getContent()).hasSize(1);
        assertThat(found.getContent().get(0).getEmail()).isEqualTo("unique@example.com");
    }

    @Test
    public void whenSearchUsers_withNoMatch_thenReturnEmptyPage() {
        // given
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        userRepository.save(user);

        // when
        Page<User> found = userRepository.searchUsers("nonexistent", PageRequest.of(0, 10));

        // then
        assertThat(found.getContent()).isEmpty();
    }
}

