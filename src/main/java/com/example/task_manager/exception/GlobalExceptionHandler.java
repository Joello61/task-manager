package com.example.task_manager.exception;

import com.example.task_manager.dto.ApiResponse;
import com.example.task_manager.entity.ApiResponseBuilder;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleUserNotFoundException(
            final UserNotFoundException ex) {
        log.warn("Utilisateur non trouvé : {}", ex.getMessage());
        return ApiResponseBuilder.error(ex.getMessage(), HttpStatus.NOT_FOUND, null);
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleTaskNotFoundException(
            final TaskNotFoundException ex) {
        log.warn("Tâche non trouvée : {}", ex.getMessage());
        return ApiResponseBuilder.error(ex.getMessage(), HttpStatus.NOT_FOUND, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(
            final MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        log.warn("Échec de validation des arguments : {}", errors);
        return ApiResponseBuilder.error("Validation failed", HttpStatus.BAD_REQUEST, errors);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ApiResponse<String>> handleUserAlreadyExistException(
            final UserAlreadyExistException ex) {
        log.warn("Tentative de création d'un utilisateur déjà existant : {}", ex.getMessage());
        return ApiResponseBuilder.error(ex.getMessage(), HttpStatus.BAD_REQUEST, null);
    }

    @ExceptionHandler(TaskAlreadyExistException.class)
    public ResponseEntity<ApiResponse<String>> handleTaskAlreadyExistException(
            final TaskAlreadyExistException ex) {
        log.warn("Tentative de création d'une tâche déjà existante : {}", ex.getMessage());
        return ApiResponseBuilder.error(ex.getMessage(), HttpStatus.BAD_REQUEST, null);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<String>> handleConstraintViolationException(
            final ConstraintViolationException ex) {
        log.warn("Violation de contrainte d'intégrité : {}", ex.getMessage());
        return ApiResponseBuilder.error(ex.getMessage(), HttpStatus.BAD_REQUEST, null);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<String>> handleBadCredentials() {
        log.warn("Échec d'authentification : identifiants incorrects");
        return ApiResponseBuilder.error(
                "Email ou mot de passe incorrect",
                HttpStatus.UNAUTHORIZED,
                null
        );
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ApiResponse<String>> handleAccountLockedException(LockedException ex) {
        log.error("Tentative de connexion sur un compte bloqué : {}", ex.getMessage());
        return ApiResponseBuilder.error(
                "Compte de l'utilisateur bloqué. Veuillez contactez un administrateur.",
                HttpStatus.UNAUTHORIZED,
                null
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<String>> handleAuthenticationException(AuthenticationException ex) {
        log.error("Erreur d'authentification inattendue : {}", ex.getMessage());
        return ApiResponseBuilder.error(
                "Erreur d'authentification",
                HttpStatus.UNAUTHORIZED,
                null
        );
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<ApiResponse<String>> handleInsufficientAuth() {
        log.warn("Accès refusé : Authentification requise pour cette ressource");
        return ApiResponseBuilder.error(
                "Authentification requise",
                HttpStatus.UNAUTHORIZED,
                null
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<String>> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("Accès refusé (Forbidden) : l'utilisateur n'a pas les droits suffisants. {}", ex.getMessage());
        return ApiResponseBuilder.error(
                "Accès refusé : vous n'avez pas les permissions nécessaires pour effectuer cette action.",
                HttpStatus.FORBIDDEN,
                null
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGlobalException(Exception ex) {
        log.error("UNE ERREUR CRITIQUE INTERNE EST SURVENUE : ", ex);
        return ApiResponseBuilder.error(
                "Une erreur interne est survenue sur le serveur",
                HttpStatus.INTERNAL_SERVER_ERROR,
                null
        );
    }

}