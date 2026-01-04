package com.katya.test.productrestassignement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.katya.test.productrestassignement.dto.UserDTO;
import com.katya.test.productrestassignement.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllUsers_ShouldReturnPagedUsers() throws Exception {
        // Given
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("johndoe");
        userDTO.setEmail("john@example.com");
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");

        Page<UserDTO> page = new PageImpl<>(Collections.singletonList(userDTO), PageRequest.of(0, 10), 1);
        when(userService.getAllUsers(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].username").value("johndoe"));
    }

    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        // Given
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("johndoe");
        userDTO.setEmail("john@example.com");
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");

        when(userService.getUserById(1L)).thenReturn(userDTO);

        // When & Then
        mockMvc.perform(get("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void createUser_ShouldReturnCreatedUser() throws Exception {
        // Given
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("johndoe");
        userDTO.setEmail("john@example.com");
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");

        when(userService.createUser(any(UserDTO.class))).thenReturn(userDTO);

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("johndoe"));
    }

    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}

