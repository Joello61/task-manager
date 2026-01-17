package com.example.task_manager.controller;

import com.example.task_manager.dto.task.CreateTaskDto;
import com.example.task_manager.dto.task.TaskResponseDto;
import com.example.task_manager.dto.user.UserResponseDto;
import com.example.task_manager.exception.TaskAlreadyExistException;
import com.example.task_manager.exception.UserNotFoundException;
import com.example.task_manager.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

    private UserResponseDto userResponse;
    private TaskResponseDto taskResponse;

    @BeforeEach
    void setUp() {
        userResponse = new UserResponseDto(1L, "Joel", "joel@example.com", "ROLE_USER");
        taskResponse = new TaskResponseDto(1L, "Apprendre le Testing", "Finir la semaine 4", false, userResponse);
    }

    @Test
    void testCreateTask_success() throws Exception {
        CreateTaskDto dto = new CreateTaskDto();
        dto.setTitle("Titre Test");
        dto.setUserId(1L);

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

}
