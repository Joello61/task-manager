package com.example.task_manager.enumeration;

import lombok.Getter;

@Getter
public enum Role {
    USER("Utilisateur Standard"),
    ADMIN("Administrateur"),
    MODERATOR("Modérateur");

    private final String description;

    Role(String description) {
        this.description = description;
    }

}
