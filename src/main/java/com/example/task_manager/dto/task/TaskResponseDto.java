package com.example.task_manager.dto.task;

import com.example.task_manager.dto.user.UserResponseDto;

public record TaskResponseDto(
        Long id, String title, String description, boolean done, UserResponseDto user
) {
}
