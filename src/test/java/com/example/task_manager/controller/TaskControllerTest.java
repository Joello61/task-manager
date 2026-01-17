package com.example.task_manager.controller;

import com.example.task_manager.dto.task.TaskResponseDto;
import com.example.task_manager.dto.user.UserResponseDto;
import com.example.task_manager.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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



}
