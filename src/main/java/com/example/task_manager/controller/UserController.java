package com.example.task_manager.controller;

import com.example.task_manager.dto.ApiResponse;
import com.example.task_manager.dto.user.CreateUserDto;
import com.example.task_manager.dto.user.UpdateUserDto;
import com.example.task_manager.dto.user.UserResponseDto;
import com.example.task_manager.entity.ApiResponseBuilder;
import com.example.task_manager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Users", description = "Gestion des utilisateurs")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/all")
    @Operation(
            summary = "Récupérer tous les utilisateurs",
            description = "Retourne la liste complète de tous les utilisateurs enregistrés dans le système"
    )
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAllUsers() {
        List<UserResponseDto> users = userService.findAll();
        return ApiResponseBuilder.success(users, "Liste des utilisateurs récupérées");
    }

    @GetMapping(value = "/{id}")
    @Operation(
            summary = "Récupérer un utilisateur par son ID",
            description = "Retourne les informations d'un utilisateur spécifique à partir de son identifiant"
    )
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(
            @PathVariable
            @Min(value = 1, message = "L'id doit être supérieur à 0")
            Long id
    ) {
        return ApiResponseBuilder.success(userService.findById(id), "Utilisateur récupéré avec succès");
    }

    @GetMapping(value = "/email/{email}")
    @Operation(
            summary = "Récupérer un utilisateur par son email",
            description = "Recherche un utilisateur à partir de son adresse email"
    )
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
    @Operation(
            summary = "Créer un nouvel utilisateur",
            description = "Permet de créer un nouvel utilisateur avec un rôle spécifique (USER ou ADMIN)"
    )
    public ResponseEntity<ApiResponse<UserResponseDto>> createUser(@Valid @RequestBody CreateUserDto userDto) {
        UserResponseDto user = userService.save(userDto);
        return ApiResponseBuilder.success(user, "Utilisateur créé avec succès");
    }

    @PatchMapping(value = "/update/{id}")
    @Operation(
            summary = "Modifier un utilisateur",
            description = "Met à jour les informations d'un utilisateur existant (nom et email uniquement)"
    )
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(
            @Valid @RequestBody UpdateUserDto userDto,
            @PathVariable
            @Min(value = 1, message = "L'id doit être supérieur à 0")
            Long id
    ) {
        return ApiResponseBuilder.success(userService.update(id, userDto), "Utilisateur modifié avec succès");
    }
}
