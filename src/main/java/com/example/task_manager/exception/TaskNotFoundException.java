package com.example.task_manager.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(Long id) {
        super("La tâche avec l'id " + id + " n'existe pas");
    }
}
