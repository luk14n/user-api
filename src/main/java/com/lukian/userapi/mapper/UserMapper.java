package com.lukian.userapi.mapper;

import com.lukian.userapi.config.MapperConfig;
import com.lukian.userapi.dto.UpdateUserRequestDto;
import com.lukian.userapi.dto.UserRegisterRequestDto;
import com.lukian.userapi.dto.UserResponseDto;
import com.lukian.userapi.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(config = MapperConfig.class)
@Component
public interface UserMapper {

    User toModel(UserRegisterRequestDto requestDto);

    UserResponseDto toDto(User user);

    void updateFromDto(UpdateUserRequestDto requestDto,
                       @MappingTarget User userFromDb);

    void updateFromDto(UserRegisterRequestDto requestDto,
                       @MappingTarget User userFromDb);
}
