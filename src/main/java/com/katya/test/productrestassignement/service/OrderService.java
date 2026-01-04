package com.katya.test.productrestassignement.service;

import com.katya.test.productrestassignement.dto.CustomerRevenueDTO;
import com.katya.test.productrestassignement.dto.OrderDTO;
import com.katya.test.productrestassignement.entity.Order;
import com.katya.test.productrestassignement.entity.Product;
import com.katya.test.productrestassignement.entity.User;
import com.katya.test.productrestassignement.exception.ResourceNotFoundException;
import com.katya.test.productrestassignement.mapper.OrderMapper;
import com.katya.test.productrestassignement.repository.OrderRepository;
import com.katya.test.productrestassignement.repository.ProductRepository;
import com.katya.test.productrestassignement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    @Transactional(readOnly = true)
    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(orderMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        return orderMapper.toDTO(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderDTO> getOrdersByUserId(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable)
                .map(orderMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<OrderDTO> getOrdersByFilters(Long userId, Order.OrderStatus status, Pageable pageable) {
        return orderRepository.findByFilters(userId, status, pageable)
                .map(orderMapper::toDTO);
    }

    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        // Verify user exists
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", orderDTO.getUserId()));

        // Verify all products exist
        List<Product> products = productRepository.findAllById(orderDTO.getProductIds());
        if (products.size() != orderDTO.getProductIds().size()) {
            throw new ResourceNotFoundException("One or more products not found");
        }

        // Calculate total amount
        BigDecimal totalAmount = products.stream()
                .map(Product::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Create order
        Order order = new Order();
        order.setUser(user);
        order.setProducts(products);
        order.setTotalAmount(totalAmount);
        order.setStatus(Order.OrderStatus.PENDING);

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDTO(savedOrder);
    }

    @Transactional
    public OrderDTO updateOrderStatus(Long id, Order.OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));

        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toDTO(updatedOrder);
    }

    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order", id);
        }
        orderRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<CustomerRevenueDTO> getCustomerRevenuePerYear(Long customerId) {
        // Verify user exists
        if (!userRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("User", customerId);
        }

        List<Object[]> results = orderRepository.calculateCustomerRevenuePerYear(customerId);
        List<CustomerRevenueDTO> revenueList = new ArrayList<>();

        for (Object[] result : results) {
            Integer year = (Integer) result[0];
            BigDecimal totalRevenue = (BigDecimal) result[1];
            revenueList.add(new CustomerRevenueDTO(customerId, year, totalRevenue));
        }

        return revenueList;
    }
}

