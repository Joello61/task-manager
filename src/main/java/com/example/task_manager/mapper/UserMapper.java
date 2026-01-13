package com.example.task_manager.mapper;

import com.example.task_manager.dto.user.CreateUserDto;
import com.example.task_manager.dto.user.UserResponseDto;
import com.example.task_manager.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDto toResponseDto(User user);
    User toEntity(CreateUserDto userDto);
}
