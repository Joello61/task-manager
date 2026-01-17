package com.example.task_manager.controller;

import com.example.task_manager.dto.user.CreateUserDto;
import com.example.task_manager.dto.user.UserResponseDto;
import com.example.task_manager.exception.UserAlreadyExistException;
import com.example.task_manager.exception.UserNotFoundException;
import com.example.task_manager.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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

    @Test
    public void testGetUserByEmail_success() throws Exception {

        String email = "alice@example.com";
        UserResponseDto userDto =
                new UserResponseDto(1L, "Alice", email, "ROLE_USER");

        when(userService.findByEmail(email)).thenReturn(userDto);

        mockMvc.perform(get("/api/users/email/{email}", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data.email").value(email))
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    public void testGetUserByEmail_notFound() throws Exception {

        String email = "unknown@example.com";

        when(userService.findByEmail(email))
                .thenThrow(new UserNotFoundException(email));

        mockMvc.perform(get("/api/users/email/{email}", email))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    public void testGetUserByEmail_invalidEmail() throws Exception {

        String invalidEmail = "not-an-email";

        mockMvc.perform(get("/api/users/email/{email}", invalidEmail))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.code").value(400));
    }


    @Test
    public void testGetAllUsers_nonEmptyList () throws Exception {

        List<UserResponseDto> userResponseDtoList = new ArrayList<>(List.of());

        Long userId = 1L;
        UserResponseDto userDto = new UserResponseDto(userId, "Alice", "alice@example.com", "ROLE_USER");

        userResponseDtoList.add(userDto);

        when(userService.findAll()).thenReturn(userResponseDtoList);

        mockMvc.perform(get("/api/users/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data[0].name").value("Alice"))
                .andExpect(jsonPath("$.data[0].email").value("alice@example.com"))
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    public void testGetAllUsers_emptyList () throws Exception {

        List<UserResponseDto> userResponseDtoList = new ArrayList<>(List.of());

        when(userService.findAll()).thenReturn(userResponseDtoList);

        mockMvc.perform(get("/api/users/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    public void testCreateUser_success() throws Exception {

        Long userId = 1L;
        UserResponseDto userDto = new UserResponseDto(userId, "Alice", "alice@example.com", "ROLE_USER");

        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.setName("Alice");
        createUserDto.setEmail("alice@example.com");
        createUserDto.setPassword("123456789");
        createUserDto.setRole("ROLE_USER");

        when(userService.save(any(CreateUserDto.class))).thenReturn(userDto);

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/api/users/create")
                .contentType("application/json").content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Utilisateur créé avec succès"))
                .andExpect(jsonPath("$.data.name").value("Alice"))
                .andExpect(jsonPath("$.data.email").value("alice@example.com"));
    }

    @Test
    public void testCreateUser_invalidEmail() throws Exception {

        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.setName("Alice");
        createUserDto.setEmail("alice");
        createUserDto.setPassword("123456789");
        createUserDto.setRole("ROLE_USER");

        mockMvc.perform(post("/api/users/create")
                .contentType("application/json").content(new ObjectMapper().writeValueAsString(createUserDto)))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void testCreateUser_missingRequiredField() throws Exception {

        CreateUserDto dto = new CreateUserDto();
        dto.setName("Alice");
        // email manquant
        dto.setPassword("123456789");
        dto.setRole("ROLE_USER");

        mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    public void testCreateUser_invalidPayload() throws Exception {

        mockMvc.perform(post("/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    public void testCreateUser_emailAlreadyExist() throws Exception {

        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.setName("Alice");
        createUserDto.setEmail("alice@example.com");
        createUserDto.setPassword("123456789");
        createUserDto.setRole("ROLE_USER");

        when(userService.save(any(CreateUserDto.class))).thenThrow(new UserAlreadyExistException());

        mockMvc.perform(post("/api/users/create")
                .contentType("application/json").content(new ObjectMapper().writeValueAsString(createUserDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    public void testUpdateUser_success() throws Exception {

        Long userId = 1L;

        UserResponseDto userDto =
                new UserResponseDto(userId, "Alice", "alice@example.com", "ROLE_USER");

        CreateUserDto updateUserDto = new CreateUserDto();
        updateUserDto.setName("Alice");
        updateUserDto.setEmail("alice@example.com");
        updateUserDto.setPassword("123456789");
        updateUserDto.setRole("ROLE_USER");

        when(userService.update(eq(userId), any(CreateUserDto.class)))
                .thenReturn(userDto);

        mockMvc.perform(patch("/api/users/update/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.name").value("Alice"))
                .andExpect(jsonPath("$.data.email").value("alice@example.com"));
    }

    @Test
    public void testUpdateUser_invalidId() throws Exception {

        Long userId = -1L;

        CreateUserDto dto = new CreateUserDto();
        dto.setName("Alice");
        dto.setEmail("alice@example.com");
        dto.setPassword("123456789");
        dto.setRole("ROLE_USER");

        mockMvc.perform(patch("/api/users/update/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.code").value(400));
    }


}
