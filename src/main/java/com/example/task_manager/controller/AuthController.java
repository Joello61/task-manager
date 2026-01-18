package com.example.task_manager.controller;

import com.example.task_manager.dto.ApiResponse;
import com.example.task_manager.dto.auth.ChangePasswordDto;
import com.example.task_manager.dto.auth.LoginDto;
import com.example.task_manager.dto.auth.LoginResponseDto;
import com.example.task_manager.dto.auth.RegisterDto;
import com.example.task_manager.entity.ApiResponseBuilder;
import com.example.task_manager.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterDto registerDto) {
        authService.register(registerDto);
        return ApiResponseBuilder.success(null, "Utilisateur créé avec succès ! Vous pouvez maintenant vous connecter.");
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody LoginDto loginDto) {
        LoginResponseDto loginResponseDto = authService.login(loginDto);
        return ApiResponseBuilder.success(loginResponseDto, "Utilisateur connecté");
    }

    @PatchMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto) {
        authService.changePassword(changePasswordDto);
        return ApiResponseBuilder.success(null, "Votre mot de passe a été modifié avec succès");
    }

}
