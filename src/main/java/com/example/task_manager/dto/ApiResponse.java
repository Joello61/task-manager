package com.example.task_manager.dto;

public record ApiResponse<T>(
        boolean status, String message, T data, int code
) {
}
