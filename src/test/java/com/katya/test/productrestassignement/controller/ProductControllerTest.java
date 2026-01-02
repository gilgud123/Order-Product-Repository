package com.katya.test.productrestassignement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.katya.test.productrestassignement.dto.ProductDTO;
import com.katya.test.productrestassignement.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllProducts_ShouldReturnPagedProducts() throws Exception {
        // Given
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setName("Laptop");
        productDTO.setDescription("High-performance laptop");
        productDTO.setPrice(new BigDecimal("999.99"));
        productDTO.setStockQuantity(50);
        productDTO.setCategory("Electronics");

        Page<ProductDTO> page = new PageImpl<>(Collections.singletonList(productDTO), PageRequest.of(0, 10), 1);
        when(productService.getAllProducts(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Laptop"))
                .andExpect(jsonPath("$.content[0].price").value(999.99))
                .andExpect(jsonPath("$.content[0].category").value("Electronics"));
    }

    @Test
    void getProductById_ShouldReturnProduct() throws Exception {
        // Given
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setName("Laptop");
        productDTO.setDescription("High-performance laptop");
        productDTO.setPrice(new BigDecimal("999.99"));
        productDTO.setStockQuantity(50);
        productDTO.setCategory("Electronics");

        when(productService.getProductById(1L)).thenReturn(productDTO);

        // When & Then
        mockMvc.perform(get("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(999.99))
                .andExpect(jsonPath("$.stockQuantity").value(50));
    }

    @Test
    void searchProducts_ShouldReturnMatchingProducts() throws Exception {
        // Given
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setName("Laptop");
        productDTO.setDescription("High-performance laptop");
        productDTO.setPrice(new BigDecimal("999.99"));
        productDTO.setStockQuantity(50);
        productDTO.setCategory("Electronics");

        Page<ProductDTO> page = new PageImpl<>(Collections.singletonList(productDTO), PageRequest.of(0, 10), 1);
        when(productService.searchProducts(eq("Laptop"), any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/products/search")
                        .param("query", "Laptop")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Laptop"));
    }

    @Test
    void filterProducts_ShouldReturnFilteredProducts() throws Exception {
        // Given
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setName("Laptop");
        productDTO.setDescription("High-performance laptop");
        productDTO.setPrice(new BigDecimal("999.99"));
        productDTO.setStockQuantity(50);
        productDTO.setCategory("Electronics");

        Page<ProductDTO> page = new PageImpl<>(Collections.singletonList(productDTO), PageRequest.of(0, 10), 1);
        when(productService.getProductsByFilters(eq("Electronics"), eq(new BigDecimal("500.00")), eq(new BigDecimal("1500.00")), any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/products/filter")
                        .param("category", "Electronics")
                        .param("minPrice", "500.00")
                        .param("maxPrice", "1500.00")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].category").value("Electronics"));
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct() throws Exception {
        // Given
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Laptop");
        productDTO.setDescription("High-performance laptop");
        productDTO.setPrice(new BigDecimal("999.99"));
        productDTO.setStockQuantity(50);
        productDTO.setCategory("Electronics");

        ProductDTO createdProductDTO = new ProductDTO();
        createdProductDTO.setId(1L);
        createdProductDTO.setName("Laptop");
        createdProductDTO.setDescription("High-performance laptop");
        createdProductDTO.setPrice(new BigDecimal("999.99"));
        createdProductDTO.setStockQuantity(50);
        createdProductDTO.setCategory("Electronics");

        when(productService.createProduct(any(ProductDTO.class))).thenReturn(createdProductDTO);

        // When & Then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(999.99));
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProduct() throws Exception {
        // Given
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Updated Laptop");
        productDTO.setDescription("Updated description");
        productDTO.setPrice(new BigDecimal("1099.99"));
        productDTO.setStockQuantity(30);
        productDTO.setCategory("Electronics");

        ProductDTO updatedProductDTO = new ProductDTO();
        updatedProductDTO.setId(1L);
        updatedProductDTO.setName("Updated Laptop");
        updatedProductDTO.setDescription("Updated description");
        updatedProductDTO.setPrice(new BigDecimal("1099.99"));
        updatedProductDTO.setStockQuantity(30);
        updatedProductDTO.setCategory("Electronics");

        when(productService.updateProduct(eq(1L), any(ProductDTO.class))).thenReturn(updatedProductDTO);

        // When & Then
        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Laptop"))
                .andExpect(jsonPath("$.price").value(1099.99));
    }

    @Test
    void deleteProduct_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(productService).deleteProduct(1L);

        // When & Then
        mockMvc.perform(delete("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void createProduct_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given - product with invalid data (empty name)
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("");
        productDTO.setPrice(new BigDecimal("999.99"));
        productDTO.setStockQuantity(50);

        // When & Then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProduct_WithNullPrice_ShouldReturnBadRequest() throws Exception {
        // Given - product with null price
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Laptop");
        productDTO.setStockQuantity(50);

        // When & Then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isBadRequest());
    }
}

