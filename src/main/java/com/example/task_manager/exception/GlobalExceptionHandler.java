package com.example.task_manager.exception;

import com.example.task_manager.dto.ApiResponse;
import com.example.task_manager.entity.ApiResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ApiResponse<String>> handleUserNotFoundException(UserNotFoundException ex) {
        return ApiResponseBuilder.error(ex.getMessage(), HttpStatus.NOT_FOUND, null);
    }

    @ExceptionHandler
    public ResponseEntity<ApiResponse<String>> handleTaskNotFoundException(TaskNotFoundException ex) {
        return ApiResponseBuilder.error(ex.getMessage(), HttpStatus.NOT_FOUND, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ApiResponseBuilder.error("Validation failed", HttpStatus.BAD_REQUEST, errors);
    }

    @ExceptionHandler
    public ResponseEntity<ApiResponse<String>> handleUserAlreadyExistException(UserAlreadyExistException ex) {
        return ApiResponseBuilder.error(ex.getMessage(), HttpStatus.BAD_REQUEST, null);
    }

    @ExceptionHandler
    public ResponseEntity<ApiResponse<String>> handleTaskAlreadyExistException(TaskAlreadyExistException ex) {
        return ApiResponseBuilder.error(ex.getMessage(), HttpStatus.BAD_REQUEST, null);
    }

}
