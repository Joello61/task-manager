package com.example.task_manager.service;

import com.example.task_manager.dto.task.TaskResponseDto;
import com.example.task_manager.dto.user.UserResponseDto;
import com.example.task_manager.entity.Task;
import com.example.task_manager.entity.User;
import com.example.task_manager.exception.TaskNotFoundException;
import com.example.task_manager.mapper.TaskMapper;
import com.example.task_manager.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

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

    /*@Test
    public void testFindAllTasks_nonEmptyList(){

    }

    @Test
    public void testFindAllTasks_emptyList(){

    }

    @Test
    public void testFindTasksByUser_emptyList(){

    }

    @Test
    public void testFindTasksByUser_nonEmptyList(){

    }

    @Test
    public void testSaveTask_success(){

    }

    @Test
    public void testSaveTask_FailButTaskExist(){

    }

    @Test
    public void testSaveTask_FailButUserNotFound(){

    }

    @Test
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
