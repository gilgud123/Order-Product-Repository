package com.katya.test.productrestassignement.service;

import com.katya.test.productrestassignement.dto.UserDTO;
import com.katya.test.productrestassignement.entity.User;
import com.katya.test.productrestassignement.exception.DuplicateResourceException;
import com.katya.test.productrestassignement.repository.UserRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create-drop"})
public class UserServiceTest {

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
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    public void whenGetAllUsers_thenReturnUserPage() {
        // given
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        userRepository.save(user);

        // when
        Page<UserDTO> result = userService.getAllUsers(PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUsername()).isEqualTo("testuser");
    }

    @Test
    public void whenGetUserById_thenReturnUser() {
        // given
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user = userRepository.save(user);

        // when
        UserDTO result = userService.getUserById(user.getId());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    public void whenSearchUsers_thenReturnMatchingUsers() {
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

        // when
        Page<UserDTO> result = userService.searchUsers("john", PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUsername()).isEqualTo("johndoe");
    }

    @Test
    public void whenCreateUser_thenReturnSavedUser() {
        // given
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("newuser");
        userDTO.setEmail("newuser@example.com");
        userDTO.setFirstName("New");
        userDTO.setLastName("User");

        // when
        UserDTO result = userService.createUser(userDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getUsername()).isEqualTo("newuser");

        User savedUser = userRepository.findById(result.getId()).orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("newuser");
    }

    @Test
    public void whenCreateUserWithDuplicateUsername_thenThrowException() {
        // given
        User existingUser = new User();
        existingUser.setUsername("testuser");
        existingUser.setEmail("test@example.com");
        existingUser.setFirstName("Test");
        existingUser.setLastName("User");
        userRepository.save(existingUser);

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setEmail("another@example.com");
        userDTO.setFirstName("Another");
        userDTO.setLastName("User");

        // when & then
        assertThatThrownBy(() -> userService.createUser(userDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Username already exists");
    }

    @Test
    public void whenCreateUserWithDuplicateEmail_thenThrowException() {
        // given
        User existingUser = new User();
        existingUser.setUsername("testuser");
        existingUser.setEmail("test@example.com");
        existingUser.setFirstName("Test");
        existingUser.setLastName("User");
        userRepository.save(existingUser);

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("anotheruser");
        userDTO.setEmail("test@example.com");
        userDTO.setFirstName("Another");
        userDTO.setLastName("User");

        // when & then
        assertThatThrownBy(() -> userService.createUser(userDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Email already exists");
    }

    @Test
    public void whenUpdateUser_thenReturnUpdatedUser() {
        // given
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user = userRepository.save(user);

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setEmail("test@example.com");
        userDTO.setFirstName("Updated");
        userDTO.setLastName("Name");

        // when
        UserDTO result = userService.updateUser(user.getId(), userDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Updated");
        assertThat(result.getLastName()).isEqualTo("Name");

        User updatedUser = userRepository.findById(user.getId()).orElse(null);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getFirstName()).isEqualTo("Updated");
    }

    @Test
    public void whenUpdateUserWithDuplicateUsername_thenThrowException() {
        // given
        User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setFirstName("User");
        user1.setLastName("One");
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setFirstName("User");
        user2.setLastName("Two");
        user2 = userRepository.save(user2);

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("user1"); // Trying to use user1's username
        userDTO.setEmail("user2@example.com");
        userDTO.setFirstName("User");
        userDTO.setLastName("Two");

        // when & then
        Long user2Id = user2.getId();
        assertThatThrownBy(() -> userService.updateUser(user2Id, userDTO))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Username already exists");
    }

    @Test
    public void whenDeleteUser_thenUserIsDeleted() {
        // given
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user = userRepository.save(user);

        Long userId = user.getId();

        // when
        userService.deleteUser(userId);

        // then
        assertThat(userRepository.existsById(userId)).isFalse();
    }
}

