package com.example.task_manager.exception;

public class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException() {
        super("Un utilisateur avec cet email existe déjà");
    }

}
