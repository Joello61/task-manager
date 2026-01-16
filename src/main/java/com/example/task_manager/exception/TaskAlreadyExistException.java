package com.example.task_manager.exception;

public class TaskAlreadyExistException extends RuntimeException {
    public TaskAlreadyExistException(String title) {
        super("la tâche avec le titre : " + title + " existe déjà");
    }

}
