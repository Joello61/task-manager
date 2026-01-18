package com.example.task_manager.dto.auth;

import com.example.task_manager.dto.user.UserResponseDto;

public record LoginResponseDto(
        UserResponseDto user, String token
) {
}
