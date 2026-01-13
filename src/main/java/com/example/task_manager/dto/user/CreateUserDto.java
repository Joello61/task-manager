package com.example.task_manager.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserDto {

    @NotNull
    @Size(min = 3, max = 20)
    private String name;

    @NotNull
    @Size(min = 8, max = 20)
    private String password;

    @NotNull
    @Size(min = 3, max = 20)
    private String role;

    @NotNull
    @Email(regexp = ".*@.*\\..*", message = "L'email doit être au format valide avec une extension (ex: .com)")
    @Size(min = 5, max = 50)
    private String email;

}
