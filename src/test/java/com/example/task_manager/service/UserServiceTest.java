package com.example.task_manager.service;

import com.example.task_manager.dto.user.CreateUserDto;
import com.example.task_manager.dto.user.UserResponseDto;
import com.example.task_manager.entity.User;
import com.example.task_manager.exception.UserAlreadyExistException;
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

    @Test
    public void testSaveUser_success(){

        // Préparation
        CreateUserDto createUserDto = new CreateUserDto();

        createUserDto.setName("test");
        createUserDto.setPassword("123456789");
        createUserDto.setRole("ROLE_USER");
        createUserDto.setEmail("test@example.com");

        Long idUser = 1L;
        User user = createUser(idUser, createUserDto.getName(), createUserDto.getEmail(), createUserDto.getPassword(), createUserDto.getRole());

        UserResponseDto expectedUser = new UserResponseDto(idUser, createUserDto.getName(), createUserDto.getEmail(), createUserDto.getRole());

        // Simulation du comportement
        when(userMapper.toEntity(createUserDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userMapper.toResponseDto(user)).thenReturn(expectedUser);

        // Exécution du test
        UserResponseDto actualUser = userService.save(createUserDto);

        // Vérifications
        assertNotNull(actualUser);
        assertEquals(expectedUser.name(), actualUser.name());
        assertEquals(expectedUser.email(), actualUser.email());
        assertEquals(idUser, actualUser.id());
        verify(userMapper).toEntity(createUserDto);
        verify(userMapper).toResponseDto(user);
        verify(userRepository).save(user);

    }

    @Test
    public void testSaveUser_FailButUserExist() {

        // Préparation
        CreateUserDto createUserDto = new CreateUserDto();

        createUserDto.setName("test");
        createUserDto.setPassword("123456789");
        createUserDto.setRole("ROLE_USER");
        createUserDto.setEmail("test@example.com");

        Long idUser = 1L;
        User user = createUser(idUser, createUserDto.getName(), createUserDto.getEmail(), createUserDto.getPassword(), createUserDto.getRole());

        // Simulation du comportement
        when(userRepository.findByEmail(createUserDto.getEmail())).thenReturn(Optional.of(user));

        // Vérification
        assertThrows(UserAlreadyExistException.class, () -> userService.save(createUserDto));
        verify(userRepository).findByEmail(createUserDto.getEmail());
        verify(userRepository, never()).save(user);
        verify(userMapper, never()).toResponseDto(user);

    }

    @Test
    public void testUpdateUser_success(){

        //Préparation

        Long idUser = 1L;

        User existingUser = createUser(
                idUser,
                "oldName",
                "old@example.com",
                "oldPassword",
                "ROLE_USER"
        );

        CreateUserDto updateUserDto = new CreateUserDto();

        updateUserDto.setName("test");
        updateUserDto.setPassword("123456789");
        updateUserDto.setRole("ROLE_ADMIN");
        updateUserDto.setEmail("test@example.com");

        UserResponseDto expectedUser = new UserResponseDto(idUser, updateUserDto.getName(), updateUserDto.getEmail(), updateUserDto.getRole());

        //Simulation du comportement
        when(userRepository.findById(idUser)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);
        when(userMapper.toResponseDto(existingUser)).thenReturn(expectedUser);

        // Exécution du test
        UserResponseDto actualUser = userService.update(idUser, updateUserDto);

        // Vérication
        assertEquals("test", existingUser.getName());
        assertEquals("test@example.com", existingUser.getEmail());
        assertEquals("ROLE_ADMIN", existingUser.getRole());

        assertNotNull(actualUser);
        assertEquals(expectedUser.name(), actualUser.name());
        assertEquals(expectedUser.email(), actualUser.email());
        assertEquals(idUser, actualUser.id());
        verify(userRepository).findById(idUser);
        verify(userRepository).save(existingUser);
        verify(userMapper).toResponseDto(existingUser);

    }

    @Test
    public void testUpdateUser_userNotFound(){

        //Préparation
        Long idUser = 99L;

        //Simulation du comportement
        when(userRepository.findById(idUser)).thenReturn(Optional.empty());

        //Vérification
        assertThrows(UserNotFoundException.class, () -> userService.update(idUser, new CreateUserDto()));
        verify(userRepository).findById(idUser);
        verify(userRepository, never()).save(any());

    }

    @Test
    public void testDeleteUser_success(){

        // Préparation
        Long idUser = 1L;

        // Simulation du comportement
        when(userRepository.existsById(idUser)).thenReturn(true);
        doNothing().when(userRepository).deleteById(idUser);

        // Test
        userService.delete(idUser);

        // Vérification
        verify(userRepository).deleteById(idUser);
    }

    @Test
    public void testDeleteUser_userNotFound(){

        // Préparation
        Long idUser = 1L;

        // Simulation du comportement
        when(userRepository.existsById(idUser)).thenReturn(false);

        // Vérification
        assertThrows(UserNotFoundException.class, () -> userService.delete(idUser));
        verify(userRepository).existsById(idUser);
        verify(userRepository, never()).deleteById(any());
    }

}
