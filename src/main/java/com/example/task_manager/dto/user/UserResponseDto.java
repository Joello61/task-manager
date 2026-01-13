package com.example.task_manager.dto.user;

public record UserResponseDto(
        Long id, String name, String email, String role
) {
}
