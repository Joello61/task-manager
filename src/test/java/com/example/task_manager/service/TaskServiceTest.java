package com.example.task_manager.service;

import com.example.task_manager.dto.task.CreateTaskDto;
import com.example.task_manager.dto.task.TaskResponseDto;
import com.example.task_manager.dto.user.UserResponseDto;
import com.example.task_manager.entity.Task;
import com.example.task_manager.entity.User;
import com.example.task_manager.enumeration.Role;
import com.example.task_manager.exception.TaskAlreadyExistException;
import com.example.task_manager.exception.TaskNotFoundException;
import com.example.task_manager.exception.UserNotFoundException;
import com.example.task_manager.mapper.TaskMapper;
import com.example.task_manager.repository.TaskRepository;
import com.example.task_manager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskMapper taskMapper;

    private User createTestUser(Long id) {
        return User.builder()
                .id(id)
                .name("test")
                .email("test@example.com")
                .password("encoded_password")
                .role(Role.USER) // Utilisation de l'Enum Role
                .dateCreation(Instant.now())
                .enabled(true)
                .build();
    }

    private Task createTestTask(Long id, String title, String description, boolean done, User user) {
        return Task.builder()
                .id(id)
                .title(title)
                .description(description)
                .done(done)
                .user(user)
                .dateCreation(Instant.now())
                .build();
    }

    @Test
    public void testFindTaskById_success(){

        // Préparation
        Long idTask = 1L;
        User user = createTestUser(1L);
        Task task = createTestTask(idTask, "test", "test", false, user);

        UserResponseDto userResponseDtoTask = new UserResponseDto(1L, "test", "test@example.com", "USER");

        TaskResponseDto expectedTask = new TaskResponseDto(idTask, "test", "test", false, userResponseDtoTask);

        // Simulation du comportement
        when(taskRepository.findById(idTask)).thenReturn(Optional.of(task));
        when(taskMapper.toResponseDto(task)).thenReturn(expectedTask);

        // Test
        TaskResponseDto actualTask = taskService.findById(idTask);

        // Vérification
        assertNotNull(actualTask);
        assertEquals("test", actualTask.title());
        assertEquals("test", actualTask.description());
        assertFalse(actualTask.done());
        assertEquals(1L, actualTask.user().id());
        assertEquals("test@example.com", actualTask.user().email());

        verify(taskRepository).findById(idTask);

    }

    @Test
    public void testFindTaskById_taskNotFound(){

        // Préparation
        Long idTask = 99L;

        // Simulation du comportement
        when(taskRepository.findById(idTask)).thenReturn(Optional.empty());

        //Test
        assertThrows(TaskNotFoundException.class, () -> taskService.findById(idTask));

    }

    @Test
    public void testFindAllTasks_nonEmptyList() {
        // Préparation
        Long userId = 1L;
        User user = createTestUser(userId);
        Task task = createTestTask(1L, "Task 1", "Description 1", false, user);
        List<Task> taskList = List.of(task);

        UserResponseDto userResponseDto = new UserResponseDto(user.getId(), user.getName(), user.getEmail(), user.getRole().name());
        TaskResponseDto expectedTask = new TaskResponseDto(task.getId(), task.getTitle(), task.getDescription(), task.isDone(), userResponseDto);

        // Simulation du comportement
        when(taskRepository.findAll()).thenReturn(taskList);
        when(taskMapper.toResponseDto(task)).thenReturn(expectedTask);

        // Exécution
        List<TaskResponseDto> actualTasks = taskService.findAll();

        // Vérification
        assertNotNull(actualTasks);
        assertEquals(1, actualTasks.size());
        assertEquals("Task 1", actualTasks.getFirst().title());
        assertEquals("Description 1", actualTasks.getFirst().description());
        assertFalse(actualTasks.getFirst().done());
        assertEquals("test@example.com", actualTasks.getFirst().user().email());

        verify(taskRepository).findAll();
        verify(taskMapper).toResponseDto(task);
    }

    @Test
    public void testFindAllTasks_emptyList() {
        // Préparation
        List<Task> taskList = List.of();

        // Simulation du comportement
        when(taskRepository.findAll()).thenReturn(taskList);

        // Exécution
        List<TaskResponseDto> actualTasks = taskService.findAll();

        // Vérification
        assertNotNull(actualTasks);
        assertTrue(actualTasks.isEmpty());

        verify(taskRepository).findAll();
        // taskMapper.toResponseDto ne doit jamais être appelé
        verify(taskMapper, never()).toResponseDto(any());
    }


    @Test
    public void testFindTasksByUser_emptyList() {
        // Préparation
        Long userId = 1L;
        User user = createTestUser(userId);

        // Simulation du comportement
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.findAllByUser(user)).thenReturn(List.of());

        // Exécution
        List<TaskResponseDto> actualTasks = taskService.findByUser(userId);

        // Vérification
        assertNotNull(actualTasks);
        assertTrue(actualTasks.isEmpty());

        verify(userRepository).findById(userId);
        verify(taskRepository).findAllByUser(user);
        verify(taskMapper, never()).toResponseDto(any());
    }

    @Test
    public void testFindTasksByUser_nonEmptyList() {
        // Préparation
        Long userId = 1L;
        User user = createTestUser(userId);
        Task task = createTestTask(1L, "Task 1", "Description 1", false, user);
        List<Task> taskList = List.of(task);

        UserResponseDto userResponseDto = new UserResponseDto(user.getId(), user.getName(), user.getEmail(), user.getRole().name());
        TaskResponseDto expectedTask = new TaskResponseDto(task.getId(), task.getTitle(), task.getDescription(), task.isDone(), userResponseDto);

        // Simulation du comportement
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.findAllByUser(user)).thenReturn(taskList);
        when(taskMapper.toResponseDto(task)).thenReturn(expectedTask);

        // Exécution
        List<TaskResponseDto> actualTasks = taskService.findByUser(userId);

        // Vérification
        assertNotNull(actualTasks);
        assertEquals(1, actualTasks.size());
        assertEquals("Task 1", actualTasks.getFirst().title());
        assertEquals("Description 1", actualTasks.getFirst().description());
        assertFalse(actualTasks.getFirst().done());
        assertEquals("test@example.com", actualTasks.getFirst().user().email());

        verify(userRepository).findById(userId);
        verify(taskRepository).findAllByUser(user);
        verify(taskMapper).toResponseDto(task);
    }


    @Test
    public void testSaveTask_success() {
        // Préparation
        Long userId = 1L;
        CreateTaskDto taskDto = new CreateTaskDto();
        taskDto.setTitle("Task 1");
        taskDto.setDescription("Description 1");
        taskDto.setDone(false);
        taskDto.setUserId(userId);

        User user = createTestUser(userId);
        Task task = createTestTask(1L, taskDto.getTitle(), taskDto.getDescription(), taskDto.isDone(), user);

        UserResponseDto userResponseDto = new UserResponseDto(user.getId(), user.getName(), user.getEmail(), user.getRole().name());
        TaskResponseDto expectedTask = new TaskResponseDto(task.getId(), task.getTitle(), task.getDescription(), task.isDone(), userResponseDto);

        // Simulation du comportement
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.findByTitle(taskDto.getTitle())).thenReturn(Optional.empty());
        when(taskMapper.toEntity(taskDto)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toResponseDto(task)).thenReturn(expectedTask);

        // Exécution
        TaskResponseDto actualTask = taskService.save(taskDto);

        // Vérification
        assertNotNull(actualTask);
        assertEquals(expectedTask.title(), actualTask.title());
        assertEquals(expectedTask.description(), actualTask.description());
        assertFalse(actualTask.done());
        assertEquals(userId, actualTask.user().id());

        verify(userRepository).findById(userId);
        verify(taskRepository).findByTitle(taskDto.getTitle());
        verify(taskRepository).save(task);
        verify(taskMapper).toEntity(taskDto);
        verify(taskMapper).toResponseDto(task);
    }

    @Test
    public void testSaveTask_FailButTaskExist() {
        // Préparation
        Long userId = 1L;
        CreateTaskDto taskDto = new CreateTaskDto();
        taskDto.setTitle("Task 1");
        taskDto.setDescription("Description 1");
        taskDto.setDone(false);
        taskDto.setUserId(userId);

        User user = createTestUser(userId);
        Task existingTask = createTestTask(1L, taskDto.getTitle(), taskDto.getDescription(), taskDto.isDone(), user);

        // Simulation du comportement
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.findByTitle(taskDto.getTitle())).thenReturn(Optional.of(existingTask));

        // Vérification
        assertThrows(TaskAlreadyExistException.class, () -> taskService.save(taskDto));

        verify(userRepository).findById(userId);
        verify(taskRepository).findByTitle(taskDto.getTitle());
        verify(taskMapper, never()).toEntity(any());
        verify(taskRepository, never()).save(any());
    }

    @Test
    public void testSaveTask_FailButUserNotFound() {
        // Préparation
        Long userId = 99L;
        CreateTaskDto taskDto = new CreateTaskDto();
        taskDto.setTitle("Task 1");
        taskDto.setDescription("Description 1");
        taskDto.setDone(false);
        taskDto.setUserId(userId);

        // Simulation du comportement
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Vérification
        assertThrows(UserNotFoundException.class, () -> taskService.save(taskDto));

        verify(userRepository).findById(userId);
        verify(taskRepository, never()).findByTitle(any());
        verify(taskMapper, never()).toEntity(any());
        verify(taskRepository, never()).save(any());
    }


    @Test
    public void testUpdateTask_success() {
        // Préparation
        Long taskId = 1L;
        Long userId = 1L;
        User user = createTestUser(userId);
        Task existingTask = createTestTask(taskId, "Old", "Old", false, user);

        CreateTaskDto updateTaskDto = new CreateTaskDto();
        updateTaskDto.setTitle("New Title");
        updateTaskDto.setDescription("New Description");
        updateTaskDto.setDone(true);
        updateTaskDto.setUserId(userId);

        TaskResponseDto expectedTask = new TaskResponseDto(taskId, "New Title", "New Description", true,
                new UserResponseDto(userId, "oldName", "old@example.com", "ROLE_USER"));

        // Simulation du comportement
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.save(existingTask)).thenReturn(existingTask);
        when(taskMapper.toResponseDto(existingTask)).thenReturn(expectedTask);

        // Exécution
        TaskResponseDto actualTask = taskService.update(taskId, updateTaskDto);

        // Vérifications
        assertEquals("New Title", existingTask.getTitle());
        assertEquals("New Description", existingTask.getDescription());
        assertTrue(existingTask.isDone());
        assertEquals(userId, existingTask.getUser().getId());

        assertNotNull(actualTask);
        assertEquals(expectedTask.title(), actualTask.title());
        assertEquals(expectedTask.description(), actualTask.description());
        assertTrue(actualTask.done());

        verify(taskRepository).findById(taskId);
        verify(userRepository).findById(userId);
        verify(taskRepository).save(existingTask);
        verify(taskMapper).toResponseDto(existingTask);
    }

    @Test
    public void testUpdateTask_taskNotFound() {
        // Préparation
        Long taskId = 99L;
        CreateTaskDto updateTaskDto = new CreateTaskDto();

        // Simulation du comportement
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // Vérification
        assertThrows(TaskNotFoundException.class, () -> taskService.update(taskId, updateTaskDto));

        verify(taskRepository).findById(taskId);
        verify(userRepository, never()).findById(any());
        verify(taskRepository, never()).save(any());
        verify(taskMapper, never()).toResponseDto(any());
    }

    @Test
    public void testUpdateTask_newUserNotFound() {
        // Préparation
        Long taskId = 1L;
        Long newUserId = 99L;

        Task existingTask = createTestTask(taskId, "Old Title", "Old Description", false, null);
        CreateTaskDto updateTaskDto = new CreateTaskDto();
        updateTaskDto.setTitle("New Title");
        updateTaskDto.setDescription("New Description");
        updateTaskDto.setDone(true);
        updateTaskDto.setUserId(newUserId);

        // Simulation du comportement
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(userRepository.findById(newUserId)).thenReturn(Optional.empty());

        // Vérification
        assertThrows(UserNotFoundException.class, () -> taskService.update(taskId, updateTaskDto));

        verify(taskRepository).findById(taskId);
        verify(userRepository).findById(newUserId);
        verify(taskRepository, never()).save(any());
        verify(taskMapper, never()).toResponseDto(any());
    }


    @Test
    public void testDeleteTask_success() {
        // Préparation
        Long taskId = 1L;

        // Simulation du comportement
        when(taskRepository.existsById(taskId)).thenReturn(true);

        // Exécution
        taskService.delete(taskId);

        // Vérification
        verify(taskRepository).existsById(taskId);
        verify(taskRepository).deleteById(taskId);
    }

    @Test
    public void testDeleteTask_taskNotFound() {
        // Préparation
        Long taskId = 99L;

        // Simulation du comportement
        when(taskRepository.existsById(taskId)).thenReturn(false);

        // Vérification
        assertThrows(TaskNotFoundException.class, () -> taskService.delete(taskId));

        verify(taskRepository).existsById(taskId);
        verify(taskRepository, never()).deleteById(any());
    }


}
