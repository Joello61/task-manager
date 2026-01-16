package com.example.task_manager.service;

import com.example.task_manager.mapper.TaskMapper;
import com.example.task_manager.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    /*@Test
    public void testFindTaskById_success(){

    }

    @Test
    public void testFindTaskById_taskNotFound(){

    }

    @Test
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
