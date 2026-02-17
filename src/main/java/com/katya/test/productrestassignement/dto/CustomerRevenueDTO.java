package com.katya.test.productrestassignement.dto;

import java.math.BigDecimal;

public record CustomerRevenueDTO(
        Long customerId,
        Integer year,
        BigDecimal totalRevenue
) {}

