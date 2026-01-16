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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    private User createUser(Long id, String name, String email, String password, String role){
        User user = new User();

        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);

        return user;
    }

    @Test
    public void testFindUserById_success(){
        //préparation
        Long idUser = 1L;
        User user = createUser(idUser, "test", "test@example.com", "123456789", "ROLE_USER");

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

    @Test
    public void testFindAllUsers_nonEmptyList() {
        // Préparation
        User user = createUser(1L, "test", "test@example.com", "123456789", "ROLE_USER");
        List<User> userList = List.of(user);

        UserResponseDto expectedUserDto = new UserResponseDto(1L, "test", "test@example.com", "ROLE_USER");
        List<UserResponseDto> expectedUserResponseDtoList = List.of(expectedUserDto);

        // Simulation du comportement des mocks
        when(userRepository.findAll()).thenReturn(userList);
        when(userMapper.toResponseDto(user)).thenReturn(expectedUserDto);

        // Exécution du test
        List<UserResponseDto> actualUserResponseDtoList = userService.findAll();

        // Vérifications
        assertNotNull(actualUserResponseDtoList);
        assertEquals(expectedUserResponseDtoList.size(), actualUserResponseDtoList.size());
        assertEquals(expectedUserResponseDtoList.getFirst().name(), actualUserResponseDtoList.getFirst().name());
        assertEquals(expectedUserResponseDtoList.getFirst().email(), actualUserResponseDtoList.getFirst().email());
        assertEquals(expectedUserResponseDtoList.getFirst().role(), actualUserResponseDtoList.getFirst().role());

        // Vérification des interactions avec les mocks
        verify(userRepository).findAll();
        verify(userMapper).toResponseDto(user);
    }


    @Test
    public void testFindAllUsers_emptyList() {
        // Préparation : une liste vide
        List<User> userList = List.of();

        // Simulation du comportement des mocks
        when(userRepository.findAll()).thenReturn(userList);

        // Exécution du test
        List<UserResponseDto> actualUserResponseDtoList = userService.findAll();

        // Vérifications
        assertNotNull(actualUserResponseDtoList);
        assertTrue(actualUserResponseDtoList.isEmpty(), "La liste retournée doit être vide");

        // Vérification des interactions avec les mocks
        verify(userRepository).findAll();
    }


    @Test
    public void testFindUserByEmail_success(){
        //préparation
        Long idUser = 1L;
        User user = createUser(idUser, "test", "test@example.com", "123456789", "ROLE_USER");

        UserResponseDto expectedUser = new UserResponseDto(idUser, "test", "test@example.com", "ROLE_USER");

        //Simulation du comportement
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userMapper.toResponseDto(user)).thenReturn(expectedUser);

        //Exécution du test
        UserResponseDto actualUser = userService.findByEmail("test@example.com");

        //Vérification
        assertNotNull(actualUser);
        assertEquals("test", actualUser.name());
        assertEquals("test@example.com", actualUser.email());
        assertEquals("ROLE_USER", actualUser.role());

        verify(userRepository).findByEmail("test@example.com");
        verify(userMapper).toResponseDto(user);
    }

    @Test
    public void testFindUserByEmail_userNotFound(){
        //preparation
        String emailUser = "noemail@example.com";

        //Simulation du comportement
        when(userRepository.findByEmail(emailUser)).thenReturn(Optional.empty());

        //Vérification
        assertThrows(UserNotFoundException.class, () -> userService.findByEmail(emailUser));
        verify(userRepository).findByEmail(emailUser);
        verify(userMapper, never()).toResponseDto(any());
    }

    /*@Test
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
