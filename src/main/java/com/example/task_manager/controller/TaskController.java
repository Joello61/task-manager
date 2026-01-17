package com.example.task_manager.controller;

import com.example.task_manager.dto.ApiResponse;
import com.example.task_manager.dto.task.CreateTaskDto;
import com.example.task_manager.dto.task.TaskResponseDto;
import com.example.task_manager.entity.ApiResponseBuilder;
import com.example.task_manager.service.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@Validated
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping(value = "/create")
    public ResponseEntity<ApiResponse<TaskResponseDto>> createTask(@Valid @RequestBody CreateTaskDto taskDto) {
        return ApiResponseBuilder.success(taskService.save(taskDto), "Tache créée avec succès");
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ApiResponse<TaskResponseDto>> getTaskById(
            @PathVariable @Min(value = 1, message = "L'id doit être supérieur à 0")
            Long id
    )
    {
        return ApiResponseBuilder.success(taskService.findById(id), "Tache trouvée avec succès");
    }

    @GetMapping(value = "/user/{userId}")
    public ResponseEntity<ApiResponse<List<TaskResponseDto>>> getTaskByUser(
            @PathVariable @Min(value = 1, message = "L'id doit être supérieur à 0")
            Long userId
    )
    {
        return ApiResponseBuilder.success(taskService.findByUser(userId), "Taches trouvée avec succès");
    }

    @GetMapping(value = "/all")
    public ResponseEntity<ApiResponse<List<TaskResponseDto>>> getAllTasks() {
        return ApiResponseBuilder.success(taskService.findAll(), "Liste des taches récupéré avec succès");
    }

    @PatchMapping(value = "/update/{id}")
    public ResponseEntity<ApiResponse<TaskResponseDto>> updateTask(
            @Valid @RequestBody CreateTaskDto taskDto,
            @PathVariable @Min(value = 1, message = "L'id doit être supérieur à 0")
            Long id
    )
    {
        return ApiResponseBuilder.success(taskService.update(id, taskDto), "Tache mise à jour avec succès");
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ApiResponse<String>> deleteTask(
            @PathVariable
            @Min(value = 1, message = "L'id doit être supérieur à 0")
            Long id
    )
    {
        taskService.delete(id);
        return ApiResponseBuilder.success(null, "Tache supprimée avec succès");
    }

}
