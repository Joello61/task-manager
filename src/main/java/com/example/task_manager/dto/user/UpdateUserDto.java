package com.example.task_manager.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserDto {

    @NotNull
    @Size(min = 3, max = 20)
    private String name;

    @NotNull
    @Email(regexp = ".*@.*\\..*", message = "L'email doit être au format valide avec une extension (ex: .com)")
    @Size(min = 5, max = 50)
    private String email;

}
