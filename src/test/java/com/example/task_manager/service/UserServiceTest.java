package com.example.task_manager.service;

import com.example.task_manager.dto.user.UserResponseDto;
import com.example.task_manager.entity.User;
import com.example.task_manager.exception.UserNotFoundException;
import com.example.task_manager.mapper.UserMapper;
import com.example.task_manager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @Test
    public void testFindUserById_success(){
        //préparation
        Long idUser = 1L;
        User user = new User();

        user.setId(idUser);
        user.setName("test");
        user.setPassword("123456789");
        user.setEmail("test@example.com");
        user.setRole("ROLE_USER");

        UserResponseDto expectedUser = new UserResponseDto(idUser, "test", "test@example.com", "ROLE_USER");

        //Simulation du comportement
        when(userRepository.findById(idUser)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDto(user)).thenReturn(expectedUser);

        //Exécution du test
        UserResponseDto actualUser = userService.findById(idUser);

        //Vérification
        assertNotNull(actualUser);
        assertEquals("test", actualUser.name());
        assertEquals("test@example.com", actualUser.email());
        assertEquals("ROLE_USER", actualUser.role());
        verify(userRepository).findById(idUser);
    }

    @Test
    public void testFindUserById_userNotFound(){
        //preparation
        Long idUser = 99L;

        //Simulation du comportement
        when(userRepository.findById(idUser)).thenReturn(Optional.empty());

        //Vérification
        assertThrows(UserNotFoundException.class, () -> userService.findById(idUser));
    }

    /*@Test
    public void testFindAllUsers_nonEmptyList(){

    }

    @Test
    public void testFindAllUsers_emptyList(){

    }

    @Test
    public void testFindUserByEmail_success(){

    }

    @Test
    public void testFindUserByEmail_userNotFound(){

    }

    @Test
    public void testSaveUser_success(){

    }

    @Test
    public void testUpdateUser_success(){

    }

    @Test
    public void testUpdateUser_userNotFound(){

    }

    @Test
    public void testDeleteUser_success(){

    }

    @Test
    public void testDeleteUser_userNotFound(){

    }*/

}
