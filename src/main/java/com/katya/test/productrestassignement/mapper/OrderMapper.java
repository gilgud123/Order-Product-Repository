package com.katya.test.productrestassignement.mapper;

import com.katya.test.productrestassignement.dto.OrderDTO;
import com.katya.test.productrestassignement.entity.Order;
import com.katya.test.productrestassignement.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface OrderMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "products", target = "productIds", qualifiedByName = "mapProductsToIds")
    OrderDTO toDTO(Order order);

    @Named("mapProductsToIds")
    default List<Long> mapProductsToIds(List<Product> products) {
        if (products == null) {
            return null;
        }
        return products.stream()
                .map(Product::getId)
                .collect(Collectors.toList());
    }
}

