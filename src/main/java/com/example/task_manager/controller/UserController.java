package com.example.task_manager.controller;

import com.example.task_manager.dto.ApiResponse;
import com.example.task_manager.dto.user.CreateUserDto;
import com.example.task_manager.dto.user.UserResponseDto;
import com.example.task_manager.entity.ApiResponseBuilder;
import com.example.task_manager.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/all")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAllUsers() {
        List<UserResponseDto> users = userService.findAll();
        return ApiResponseBuilder.success(users, "Liste des utilisateurs récupérées");
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(@PathVariable Long id) {
        return ApiResponseBuilder.success(userService.findById(id), "Utilisateur récupéré avec succès");
    }

    @GetMapping(value = "/email/{email}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserByEmail(@PathVariable String email) {
        return ApiResponseBuilder.success(userService.findByEmail(email), "Utilisateur récupéré avec succès");
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ApiResponseBuilder.success(null, "Utilisateur supprimé avec succès");
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<UserResponseDto>> createUser(@Valid @RequestBody CreateUserDto userDto) {
        UserResponseDto user = userService.save(userDto);
        return ApiResponseBuilder.success(user, "Utilisateur créé avec succès");
    }

    @PatchMapping(value = "/update/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(@Valid @RequestBody CreateUserDto userDto, @PathVariable Long id) {
        return ApiResponseBuilder.success(userService.update(id, userDto), "Utilisateur modifié avec succès");
    }
}
