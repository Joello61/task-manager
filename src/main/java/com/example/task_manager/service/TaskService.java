package com.example.task_manager.service;

import com.example.task_manager.dto.task.CreateTaskDto;
import com.example.task_manager.dto.task.TaskResponseDto;
import com.example.task_manager.entity.Task;
import com.example.task_manager.entity.User;
import com.example.task_manager.exception.TaskAlreadyExistException;
import com.example.task_manager.exception.TaskNotFoundException;
import com.example.task_manager.exception.UserNotFoundException;
import com.example.task_manager.mapper.TaskMapper;
import com.example.task_manager.repository.TaskRepository;
import com.example.task_manager.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskMapper = taskMapper;
    }

    public TaskResponseDto save(CreateTaskDto taskDto) {
        User user = userRepository.findById(taskDto.getUserId()).orElseThrow(() -> new UserNotFoundException(taskDto.getUserId()));

        taskRepository.findByTitle(taskDto.getTitle()).ifPresent(t -> {
            throw new TaskAlreadyExistException(taskDto.getTitle());
        });

        Task task = taskMapper.toEntity(taskDto);
        task.setUser(user);
        Task taskSave = taskRepository.save(task);

        return taskMapper.toResponseDto(taskSave);
    }

    public List<TaskResponseDto> findAll() {
        return taskRepository.findAll().stream().map(taskMapper::toResponseDto).toList();
    }

    public TaskResponseDto findById(Long taskId) {
        return taskRepository.findById(taskId).map(taskMapper::toResponseDto).orElseThrow(() -> new TaskNotFoundException(taskId));
    }

    public List<TaskResponseDto> findByUser(Long idUser) {
        User user = userRepository.findById(idUser).orElseThrow(() -> new UserNotFoundException(idUser));
        return taskRepository.findAllByUser(user).stream().map(taskMapper::toResponseDto).toList();
    }

    public TaskResponseDto update(Long taskId, CreateTaskDto taskDto) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));
        User user = userRepository.findById(taskDto.getUserId()).orElseThrow(() -> new UserNotFoundException(taskDto.getUserId()));

        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setDone(taskDto.isDone());
        task.setUser(user);

        Task taskUpdated = taskRepository.save(task);
        return taskMapper.toResponseDto(taskUpdated);
    }

    public void delete(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        taskRepository.deleteById(id);
    }
}
