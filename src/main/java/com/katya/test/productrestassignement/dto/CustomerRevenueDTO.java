package com.katya.test.productrestassignement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRevenueDTO {
    private Long customerId;
    private Integer year;
    private BigDecimal totalRevenue;
}

