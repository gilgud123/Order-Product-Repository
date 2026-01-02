package com.katya.test.productrestassignement.dto;

import com.katya.test.productrestassignement.entity.Order;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private Long id;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotEmpty(message = "Product IDs are required")
    private List<Long> productIds;

    private BigDecimal totalAmount;

    private Order.OrderStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

