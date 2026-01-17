package com.example.task_manager.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(final Long id) {
        super("L'utilisateur avec l'id " + id + " n'existe pas");
    }

    public UserNotFoundException(final String email) {
        super("L'utilisateur avec l'email " + email + " n'existe pas");
    }
}