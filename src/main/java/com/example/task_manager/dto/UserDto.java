package com.example.task_manager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

    @NotNull
    @PositiveOrZero
    private Long id;

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
    @Email
    @Size(min = 5, max = 50)
    private String email;

}
