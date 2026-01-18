package com.example.task_manager.controller;

import com.example.task_manager.dto.ApiResponse;
import com.example.task_manager.dto.auth.ChangePasswordDto;
import com.example.task_manager.dto.auth.LoginDto;
import com.example.task_manager.dto.auth.LoginResponseDto;
import com.example.task_manager.dto.auth.RegisterDto;
import com.example.task_manager.entity.ApiResponseBuilder;
import com.example.task_manager.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Validated
@Tag(name = "Authentication", description = "Endpoints pour l'authentification et la gestion du compte utilisateur")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(
            summary = "Créer un nouveau compte utilisateur",
            description = "Permet de créer un nouveau compte utilisateur. L'email doit être unique."
    )
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterDto registerDto) {
        authService.register(registerDto);
        return ApiResponseBuilder.success(null, "Utilisateur créé avec succès ! Vous pouvez maintenant vous connecter.");
    }

    @PostMapping("/login")
    @Operation(
            summary = "Se connecter",
            description = "Permet de s'authentifier avec email et mot de passe. Retourne un token JWT à utiliser pour les requêtes authentifiées."
    )
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody LoginDto loginDto) {
        LoginResponseDto loginResponseDto = authService.login(loginDto);
        return ApiResponseBuilder.success(loginResponseDto, "Utilisateur connecté");
    }

    @PatchMapping("/change-password")
    @Operation(
            summary = "Changer son mot de passe",
            description = "Permet à un utilisateur authentifié de modifier son mot de passe. Nécessite l'ancien mot de passe.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<ApiResponse<String>> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto) {
        authService.changePassword(changePasswordDto);
        return ApiResponseBuilder.success(null, "Votre mot de passe a été modifié avec succès");
    }

}
