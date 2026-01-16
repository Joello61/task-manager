package com.example.task_manager.controller;

import com.example.task_manager.dto.user.UserResponseDto;
import com.example.task_manager.exception.UserNotFoundException;
import com.example.task_manager.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    public void testGetUserById_success() throws Exception {
        Long userId = 1L;
        UserResponseDto userDto = new UserResponseDto(userId, "Alice", "alice@example.com", "ROLE_USER");

        when(userService.findById(userId)).thenReturn(userDto);

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(true)))
                .andExpect(jsonPath("$.message", is("Utilisateur récupéré avec succès")))
                .andExpect(jsonPath("$.data.name", is("Alice")))
                .andExpect(jsonPath("$.data.email", is("alice@example.com")))
                .andExpect(jsonPath("$.data.role", is("ROLE_USER")))
                .andExpect(jsonPath("$.code", is(200)));
    }

    @Test
    public void testGetUserById_userNotFound() throws Exception {

        Long userId = 99L;

        when(userService.findById(userId))
                .thenThrow(new UserNotFoundException(userId));

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    public void testGetUserById_invalidId() throws Exception {
        Long userId = -1L;
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.code").value(400));
    }

}
