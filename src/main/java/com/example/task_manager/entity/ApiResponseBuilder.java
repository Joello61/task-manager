package com.example.task_manager.entity;

import com.example.task_manager.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponseBuilder {

    public static <T> ResponseEntity <ApiResponse<T>> success(T data, String message) {
        return ResponseEntity.ok(new ApiResponse<>(true, message, data, HttpStatus.OK.value()));
    }

    public static <T> ResponseEntity <ApiResponse<T>> error(String message, HttpStatus status, T data) {
        return ResponseEntity.status(status).body(new ApiResponse<>(false, message, data, status.value()));
    }

}
