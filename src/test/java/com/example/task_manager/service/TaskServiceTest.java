package com.example.task_manager.service;

import com.example.task_manager.dto.task.CreateTaskDto;
import com.example.task_manager.dto.task.TaskResponseDto;
import com.example.task_manager.dto.user.UserResponseDto;
import com.example.task_manager.entity.Task;
import com.example.task_manager.entity.User;
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

    private Task createTask(Long id, String title, String description, boolean done){

        User user = new User(1L, "test", "test@example.com", "123456789", "ROLE_USER", Instant.now(), List.of());

        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setDescription(description);
        task.setDone(done);
        task.setUser(user);
        return task;
    }

    @Test
    public void testFindTaskById_success(){

        // Préparation
        Long idTask = 1L;
        Task task = createTask(idTask, "test", "test", false);
        UserResponseDto userResponseDtoTask = new UserResponseDto(1L, "test", "test@example.com", "ROLE_USER");

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
        User user = new User(1L, "test", "test@example.com", "123456789", "ROLE_USER", Instant.now(), List.of());
        Task task = createTask(1L, "Task 1", "Description 1", false);
        List<Task> taskList = List.of(task);

        UserResponseDto userResponseDto = new UserResponseDto(user.getId(), user.getName(), user.getEmail(), user.getRole());
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
        User user = new User(userId, "test", "test@example.com", "123456789", "ROLE_USER", Instant.now(), List.of());

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
        User user = new User(userId, "test", "test@example.com", "123456789", "ROLE_USER", Instant.now(), List.of());
        Task task = createTask(1L, "Task 1", "Description 1", false);
        List<Task> taskList = List.of(task);

        UserResponseDto userResponseDto = new UserResponseDto(user.getId(), user.getName(), user.getEmail(), user.getRole());
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

        User user = new User(userId, "test", "test@example.com", "123456789", "ROLE_USER", Instant.now(), List.of());
        Task task = createTask(1L, taskDto.getTitle(), taskDto.getDescription(), taskDto.isDone());

        UserResponseDto userResponseDto = new UserResponseDto(user.getId(), user.getName(), user.getEmail(), user.getRole());
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

        User user = new User(userId, "test", "test@example.com", "123456789", "ROLE_USER", Instant.now(), List.of());
        Task existingTask = createTask(1L, taskDto.getTitle(), taskDto.getDescription(), taskDto.isDone());

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


   /* @Test
    public void testUpdateTask_success(){

    }

    @Test
    public void testUpdateTask_taskNotFound(){

    }

    @Test
    public void testUpdateTask_newUserNotFound(){

    }

    @Test
    public void testDeleteTask_success(){

    }

    @Test
    public void testDeleteTask_taskNotFound(){

    }*/

}
