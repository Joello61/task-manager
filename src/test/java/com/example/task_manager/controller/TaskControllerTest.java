package com.example.task_manager.controller;

import com.example.task_manager.dto.task.CreateTaskDto;
import com.example.task_manager.dto.task.TaskResponseDto;
import com.example.task_manager.dto.user.UserResponseDto;
import com.example.task_manager.exception.TaskAlreadyExistException;
import com.example.task_manager.exception.TaskNotFoundException;
import com.example.task_manager.exception.UserNotFoundException;
import com.example.task_manager.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;


    @Test
    void testCreateTask_success() throws Exception {
        Long userId = 1L;
        CreateTaskDto dto = new CreateTaskDto();
        dto.setTitle("Titre Test");
        dto.setDescription("Description Test");
        dto.setUserId(userId);

        UserResponseDto userResponse = new UserResponseDto(userId, "Joel", "joel@example.com", "ROLE_USER");
        TaskResponseDto taskResponse = new TaskResponseDto(1L, "Titre Test", "Description Test", false, userResponse);

        when(taskService.save(any(CreateTaskDto.class))).thenReturn(taskResponse);

        mockMvc.perform(post("/api/tasks/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(true)))
                .andExpect(jsonPath("$.message", is("Tache créée avec succès")))
                .andExpect(jsonPath("$.data.title", is("Titre Test")))
                .andExpect(jsonPath("$.data.user.id", is(1)))
                .andExpect(jsonPath("$.code", is(200)));
    }

    @Test
    void testCreateTask_invalidBody() throws Exception {
        CreateTaskDto dto = new CreateTaskDto(); // Title manquant

        mockMvc.perform(post("/api/tasks/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(false)))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.code", is(400)));
    }

    @Test
    void testCreateTask_userNotFound() throws Exception {
        CreateTaskDto dto = new CreateTaskDto();
        dto.setTitle("Titre");
        dto.setDescription("Description");
        dto.setUserId(99L);

        when(taskService.save(any(CreateTaskDto.class))).thenThrow(new UserNotFoundException(99L));

        mockMvc.perform(post("/api/tasks/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(false)))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.code", is(404)));
    }

    @Test
    void testCreateTask_taskAlreadyExist() throws Exception {
        CreateTaskDto dto = new CreateTaskDto();
        dto.setTitle("Titre Existant");
        dto.setUserId(1L);

        when(taskService.save(any(CreateTaskDto.class))).thenThrow(new TaskAlreadyExistException("Titre Existant"));

        mockMvc.perform(post("/api/tasks/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(false)))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.code", is(400)));
    }

    @Test
    void testGetTaskById_success() throws Exception {
        UserResponseDto userResponse = new UserResponseDto(1L, "Joel", "joel@example.com", "ROLE_USER");
        TaskResponseDto taskResponse = new TaskResponseDto(1L, "Titre Test", "Description Test", false, userResponse);

        when(taskService.findById(1L)).thenReturn(taskResponse);

        mockMvc.perform(get("/api/tasks/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.message", containsString("succès")))
                .andExpect(jsonPath("$.code", is(200)));
    }

    @Test
    void testGetTaskById_taskNotFound() throws Exception {
        when(taskService.findById(99L)).thenThrow(new TaskNotFoundException(99L));

        mockMvc.perform(get("/api/tasks/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(false)))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.code", is(404)));
    }

    @Test
    void testGetTaskById_invalidId() throws Exception {
        mockMvc.perform(get("/api/tasks/{id}", -1L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(false)))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.code", is(400)));
    }

    @Test
    void testUpdateTask_success() throws Exception {
        Long userId = 1L;
        CreateTaskDto updateDto = new CreateTaskDto();
        updateDto.setTitle("Titre Update");
        updateDto.setDescription("Desc");
        updateDto.setUserId(userId);

        UserResponseDto userResponse = new UserResponseDto(userId, "Joel", "joel@example.com", "ROLE_USER");
        TaskResponseDto updatedTask = new TaskResponseDto(1L, "Titre Update", "Desc", true, userResponse);

        when(taskService.update(eq(1L), any(CreateTaskDto.class))).thenReturn(updatedTask);

        mockMvc.perform(patch("/api/tasks/update/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(true)))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data.title", is("Titre Update")))
                .andExpect(jsonPath("$.data.done", is(true)))
                .andExpect(jsonPath("$.code", is(200)));
    }

    @Test
    void testUpdateTask_invalidBody() throws Exception {
        CreateTaskDto updateDto = new CreateTaskDto();

        mockMvc.perform(patch("/api/tasks/update/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(false)))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.code", is(400)));
    }

    @Test
    void testUpdateTask_invalidId() throws Exception {
        CreateTaskDto updateDto = new CreateTaskDto();
        updateDto.setTitle("Titre");
        updateDto.setDescription("Desc");
        updateDto.setUserId(1L);

        mockMvc.perform(patch("/api/tasks/update/{id}", -1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(false)))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.code", is(400)));
    }

    @Test
    void testUpdateTask_taskNotFound() throws Exception {
        CreateTaskDto updateDto = new CreateTaskDto();
        updateDto.setTitle("Titre");
        updateDto.setDescription("Desc");
        updateDto.setUserId(1L);

        when(taskService.update(eq(99L), any(CreateTaskDto.class))).thenThrow(new TaskNotFoundException(99L));

        mockMvc.perform(patch("/api/tasks/update/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(false)))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.code", is(404)));
    }

    @Test
    void testUpdateTask_userNotFound() throws Exception {
        CreateTaskDto updateDto = new CreateTaskDto();
        updateDto.setTitle("Titre");
        updateDto.setDescription("Desc");
        updateDto.setUserId(99L);

        when(taskService.update(eq(1L), any(CreateTaskDto.class))).thenThrow(new UserNotFoundException(99L));

        mockMvc.perform(patch("/api/tasks/update/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(false)))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.code", is(404)));
    }

    @Test
    void testDeleteTask_success() throws Exception {
        doNothing().when(taskService).delete(1L);

        mockMvc.perform(delete("/api/tasks/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(true)))
                .andExpect(jsonPath("$.message", containsString("supprimée")))
                .andExpect(jsonPath("$.code", is(200)));
    }

    @Test
    void testDeleteTask_taskNotFound() throws Exception {
        doThrow(new TaskNotFoundException(99L)).when(taskService).delete(99L);

        mockMvc.perform(delete("/api/tasks/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(false)))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.code", is(404)));
    }

    @Test
    void testDeleteTask_invalidId() throws Exception {
        mockMvc.perform(delete("/api/tasks/{id}", -1L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(false)))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.code", is(400)));
    }

}
