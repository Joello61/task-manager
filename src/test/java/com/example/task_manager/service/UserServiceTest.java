package com.example.task_manager.service;

import com.example.task_manager.dto.user.CreateUserDto;
import com.example.task_manager.dto.user.UpdateUserDto;
import com.example.task_manager.dto.user.UserResponseDto;
import com.example.task_manager.entity.User;
import com.example.task_manager.enumeration.Role;
import com.example.task_manager.exception.UserAlreadyExistException;
import com.example.task_manager.exception.UserNotFoundException;
import com.example.task_manager.mapper.UserMapper;
import com.example.task_manager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
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

    private User createTestUser(Long id, String name, String email, Role role) {
        return User.builder()
                .id(id)
                .name(name)
                .email(email)
                .password("encoded_password") // Simulé
                .role(role)
                .dateCreation(Instant.now())
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();
    }

    @Test
    public void testFindUserById_success(){
        //préparation
        Long idUser = 1L;
        User user = createTestUser(idUser, "test", "test@example.com", Role.USER);

        UserResponseDto expectedUser = new UserResponseDto(idUser, "test", "test@example.com", "USER");

        //Simulation du comportement
        when(userRepository.findById(idUser)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDto(user)).thenReturn(expectedUser);

        //Exécution du test
        UserResponseDto actualUser = userService.findById(idUser);

        //Vérification
        assertNotNull(actualUser);
        assertEquals("test", actualUser.name());
        assertEquals("test@example.com", actualUser.email());
        assertEquals("USER", actualUser.role());
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
        User user = createTestUser(1L, "test", "test@example.com", Role.USER);
        List<User> userList = List.of(user);
        Page<User> userPage = new PageImpl<>(userList);

        UserResponseDto expectedUserDto = new UserResponseDto(1L, "test", "test@example.com", "USER");
        Page<UserResponseDto> expectedUserResponseDtoPage = new PageImpl<>(List.of(expectedUserDto));

        // Simulation du comportement des mocks
        Pageable pageable = PageRequest.of(0, 5);
        when(userRepository.findAll()).thenReturn(userList);
        when(userMapper.toResponseDto(user)).thenReturn(expectedUserDto);

        // Exécution du test
        Page<UserResponseDto> actualUserPage = userService.findAll(pageable);;

        // Vérifications
        assertNotNull(actualUserPage);
        assertEquals(1, actualUserPage.getContent().size());
        assertEquals(expectedUserDto.name(), actualUserPage.getContent().getFirst().name());
        assertEquals(expectedUserDto.email(), actualUserPage.getContent().getFirst().email());
        assertEquals(expectedUserDto.role(), actualUserPage.getContent().getFirst().role());

        // Vérification des interactions avec les mocks
        verify(userRepository).findAll();
        verify(userMapper).toResponseDto(user);
    }


    @Test
    public void testFindAllUsers_emptyList() {
        // Préparation : une liste vide
        List<User> userList = List.of();
        Page<User> emptyUserPage = new PageImpl<>(userList);

        // Simulation du comportement des mocks
        Pageable pageable = PageRequest.of(0, 5);
        when(userRepository.findAll(pageable)).thenReturn(emptyUserPage);

        // Exécution du test
        Page<UserResponseDto> actualUserPage = userService.findAll(pageable);

        // Vérifications
        assertNotNull(actualUserPage);
        assertTrue(actualUserPage.isEmpty(), "La page retournée doit être vide");

        // Vérification des interactions avec les mocks
        verify(userRepository).findAll(pageable);
    }


    @Test
    public void testFindUserByEmail_success(){
        //préparation
        Long idUser = 1L;
        User user = createTestUser(idUser, "test", "test@example.com", Role.USER);

        UserResponseDto expectedUser = new UserResponseDto(idUser, "test", "test@example.com", "USER");

        //Simulation du comportement
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userMapper.toResponseDto(user)).thenReturn(expectedUser);

        //Exécution du test
        UserResponseDto actualUser = userService.findByEmail("test@example.com");

        //Vérification
        assertNotNull(actualUser);
        assertEquals("test", actualUser.name());
        assertEquals("test@example.com", actualUser.email());
        assertEquals("USER", actualUser.role());

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
        createUserDto.setRole(Role.USER);
        createUserDto.setEmail("test@example.com");

        Long idUser = 1L;
        User user = createTestUser(idUser, createUserDto.getName(), createUserDto.getEmail(), createUserDto.getRole());

        UserResponseDto expectedUser = new UserResponseDto(idUser, createUserDto.getName(), createUserDto.getEmail(), createUserDto.getRole().name());

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
        createUserDto.setRole(Role.USER);
        createUserDto.setEmail("test@example.com");

        Long idUser = 1L;
        User user = createTestUser(idUser, createUserDto.getName(), createUserDto.getEmail(), createUserDto.getRole());

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
        User existingUser = createTestUser(
                idUser,
                "oldName",
                "old@example.com",
                Role.ADMIN
        );

        UpdateUserDto updateUserDto = new UpdateUserDto();

        updateUserDto.setName("test");
        updateUserDto.setEmail("test@example.com");

        UserResponseDto expectedUser = new UserResponseDto(idUser, updateUserDto.getName(), updateUserDto.getEmail(), "ADMIN");

        //Simulation du comportement
        when(userRepository.findById(idUser)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);
        when(userMapper.toResponseDto(existingUser)).thenReturn(expectedUser);

        // Exécution du test
        UserResponseDto actualUser = userService.update(idUser, updateUserDto);

        // Vérication
        assertEquals("test", existingUser.getName());
        assertEquals("test@example.com", existingUser.getEmail());
        assertEquals("ADMIN", existingUser.getRole().name());

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
        assertThrows(UserNotFoundException.class, () -> userService.update(idUser, new UpdateUserDto()));
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
