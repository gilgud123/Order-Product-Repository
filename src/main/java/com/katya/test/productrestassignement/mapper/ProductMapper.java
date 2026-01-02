package com.katya.test.productrestassignement.mapper;

import com.katya.test.productrestassignement.dto.ProductDTO;
import com.katya.test.productrestassignement.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProductMapper {

    ProductDTO toDTO(Product product);

    Product toEntity(ProductDTO dto);

    void updateEntityFromDTO(ProductDTO dto, @MappingTarget Product product);
}

