package com.katya.test.productrestassignement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.katya.test.productrestassignement.dto.OrderDTO;
import com.katya.test.productrestassignement.entity.Order;
import com.katya.test.productrestassignement.service.OrderService;
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
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllOrders_ShouldReturnPagedOrders() throws Exception {
        // Given
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setUserId(1L);
        orderDTO.setProductIds(Arrays.asList(1L, 2L));
        orderDTO.setTotalAmount(new BigDecimal("1029.98"));
        orderDTO.setStatus(Order.OrderStatus.PENDING);

        Page<OrderDTO> page = new PageImpl<>(Collections.singletonList(orderDTO), PageRequest.of(0, 10), 1);
        when(orderService.getAllOrders(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].userId").value(1))
                .andExpect(jsonPath("$.content[0].totalAmount").value(1029.98))
                .andExpect(jsonPath("$.content[0].status").value("PENDING"));
    }

    @Test
    void getOrderById_ShouldReturnOrder() throws Exception {
        // Given
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setUserId(1L);
        orderDTO.setProductIds(Arrays.asList(1L, 2L));
        orderDTO.setTotalAmount(new BigDecimal("1029.98"));
        orderDTO.setStatus(Order.OrderStatus.PENDING);

        when(orderService.getOrderById(1L)).thenReturn(orderDTO);

        // When & Then
        mockMvc.perform(get("/api/orders/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.totalAmount").value(1029.98))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void getOrdersByUserId_ShouldReturnUserOrders() throws Exception {
        // Given
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setUserId(1L);
        orderDTO.setProductIds(Arrays.asList(1L, 2L));
        orderDTO.setTotalAmount(new BigDecimal("1029.98"));
        orderDTO.setStatus(Order.OrderStatus.PENDING);

        Page<OrderDTO> page = new PageImpl<>(Collections.singletonList(orderDTO), PageRequest.of(0, 10), 1);
        when(orderService.getOrdersByUserId(eq(1L), any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/orders/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].userId").value(1));
    }

    @Test
    void filterOrders_ShouldReturnFilteredOrders() throws Exception {
        // Given
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setUserId(1L);
        orderDTO.setProductIds(Arrays.asList(1L, 2L));
        orderDTO.setTotalAmount(new BigDecimal("1029.98"));
        orderDTO.setStatus(Order.OrderStatus.PENDING);

        Page<OrderDTO> page = new PageImpl<>(Collections.singletonList(orderDTO), PageRequest.of(0, 10), 1);
        when(orderService.getOrdersByFilters(eq(1L), eq(Order.OrderStatus.PENDING), any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/orders/filter")
                        .param("userId", "1")
                        .param("status", "PENDING")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].userId").value(1))
                .andExpect(jsonPath("$.content[0].status").value("PENDING"));
    }

    @Test
    void createOrder_ShouldReturnCreatedOrder() throws Exception {
        // Given
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setUserId(1L);
        orderDTO.setProductIds(Arrays.asList(1L, 2L));
        orderDTO.setTotalAmount(new BigDecimal("1029.98"));

        OrderDTO createdOrderDTO = new OrderDTO();
        createdOrderDTO.setId(1L);
        createdOrderDTO.setUserId(1L);
        createdOrderDTO.setProductIds(Arrays.asList(1L, 2L));
        createdOrderDTO.setTotalAmount(new BigDecimal("1029.98"));
        createdOrderDTO.setStatus(Order.OrderStatus.PENDING);

        when(orderService.createOrder(any(OrderDTO.class))).thenReturn(createdOrderDTO);

        // When & Then
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void updateOrderStatus_ShouldReturnUpdatedOrder() throws Exception {
        // Given
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setUserId(1L);
        orderDTO.setProductIds(Arrays.asList(1L, 2L));
        orderDTO.setTotalAmount(new BigDecimal("1029.98"));
        orderDTO.setStatus(Order.OrderStatus.SHIPPED);

        when(orderService.updateOrderStatus(eq(1L), eq(Order.OrderStatus.SHIPPED))).thenReturn(orderDTO);

        // When & Then
        mockMvc.perform(patch("/api/orders/1/status")
                        .param("status", "SHIPPED")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("SHIPPED"));
    }

    @Test
    void deleteOrder_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(orderService).deleteOrder(1L);

        // When & Then
        mockMvc.perform(delete("/api/orders/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void createOrder_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given - order with null userId
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setProductIds(Arrays.asList(1L, 2L));

        // When & Then
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createOrder_WithEmptyProductIds_ShouldReturnBadRequest() throws Exception {
        // Given - order with empty product list
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setUserId(1L);
        orderDTO.setProductIds(Collections.emptyList());

        // When & Then
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isBadRequest());
    }
}

