package com.example.task_manager.controller;

import com.example.task_manager.dto.ApiResponse;
import com.example.task_manager.dto.user.CreateUserDto;
import com.example.task_manager.dto.user.UserResponseDto;
import com.example.task_manager.entity.ApiResponseBuilder;
import com.example.task_manager.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/api/users")
@Validated
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
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(
            @PathVariable
            @Min(value = 1, message = "L'id doit être supérieur à 0")
            Long id
    ) {
        return ApiResponseBuilder.success(userService.findById(id), "Utilisateur récupéré avec succès");
    }

    @GetMapping(value = "/email/{email}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserByEmail(
            @PathVariable
            @NotBlank(message = "L'email ne doit pas être vide")
            @Email(
                    regexp = ".*@.*\\..*",
                    message = "L'email doit être au format valide avec une extension (ex: .com)"
            )
            String email) {
        return ApiResponseBuilder.success(userService.findByEmail(email), "Utilisateur récupéré avec succès");
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(
            @PathVariable
            @Min(value = 1, message = "L'id doit être supérieur à 0")
            Long id
    ) {
        userService.delete(id);
        return ApiResponseBuilder.success(null, "Utilisateur supprimé avec succès");
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<UserResponseDto>> createUser(@Valid @RequestBody CreateUserDto userDto) {
        UserResponseDto user = userService.save(userDto);
        return ApiResponseBuilder.success(user, "Utilisateur créé avec succès");
    }

    @PatchMapping(value = "/update/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(
            @Valid @RequestBody CreateUserDto userDto,
            @PathVariable
            @Min(value = 1, message = "L'id doit être supérieur à 0")
            Long id
    ) {
        return ApiResponseBuilder.success(userService.update(id, userDto), "Utilisateur modifié avec succès");
    }
}
