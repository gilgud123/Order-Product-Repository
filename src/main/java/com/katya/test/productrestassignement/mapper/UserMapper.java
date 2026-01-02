package com.katya.test.productrestassignement.mapper;

import com.katya.test.productrestassignement.dto.UserDTO;
import com.katya.test.productrestassignement.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {

    UserDTO toDTO(User user);

    User toEntity(UserDTO dto);

    void updateEntityFromDTO(UserDTO dto, @MappingTarget User user);
}

