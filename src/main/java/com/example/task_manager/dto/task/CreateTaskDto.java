package com.example.task_manager.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTaskDto {

    @NotNull
    @PositiveOrZero
    private Long userId;

    @NotBlank
    @Size(min = 3, max = 50)
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private boolean done;

}
