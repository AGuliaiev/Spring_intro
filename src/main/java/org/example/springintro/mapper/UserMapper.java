package org.example.springintro.mapper;

import org.example.springintro.config.MapperConfig;
import org.example.springintro.dto.UserRegistrationRequestDto;
import org.example.springintro.dto.UserResponseDto;
import org.example.springintro.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toDto(User user);

    User toModel(UserRegistrationRequestDto dto);

}