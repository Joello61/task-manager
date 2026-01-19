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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    public TaskService(final TaskRepository taskRepository,
                       final UserRepository userRepository,
                       final TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskMapper = taskMapper;
    }

    @PreAuthorize("hasRole('ADMIN') or #taskDto.userId == authentication.principal.id")
    public TaskResponseDto save(final CreateTaskDto taskDto) {
        log.info("Création d'une nouvelle tâche: '{}' pour l'utilisateur ID: {}", taskDto.getTitle(), taskDto.getUserId());
        User user = userRepository.findById(taskDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException(taskDto.getUserId()));

        taskRepository.findByTitle(taskDto.getTitle()).ifPresent(t -> {
            log.warn("Échec création tâche: titre déjà utilisé '{}'", taskDto.getTitle());
            throw new TaskAlreadyExistException(taskDto.getTitle());
        });

        Task task = taskMapper.toEntity(taskDto);
        task.setUser(user);
        Task taskSave = taskRepository.save(task);

        log.info("Tâche créée avec succès (ID: {})", taskSave.getId());
        return taskMapper.toResponseDto(taskSave);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<TaskResponseDto> findAll(Pageable pageable) {
        log.info("Récupération de toutes les tâches (Admin) - Page: {}, Taille: {}", pageable.getPageNumber(), pageable.getPageSize());
        return taskRepository.findAll(pageable).map(taskMapper::toResponseDto);
    }

    @PostAuthorize("hasRole('ADMIN') or returnObject.user.id = authentication.principal.id")
    public TaskResponseDto findById(final Long taskId) {
        log.info("Récupération de la tâche ID: {}", taskId);
        return taskRepository.findById(taskId).map(taskMapper::toResponseDto)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
    }

    @PreAuthorize("hasRole('ADMIN') or #idUser == authentication.principal.id")
    public Page<TaskResponseDto> findByUser(final Long idUser, Pageable pageable) {
        log.info("Récupération des tâches pour l'utilisateur ID: {}", idUser);
        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new UserNotFoundException(idUser));
        return taskRepository.findAllByUser(user, pageable)
                .map(taskMapper::toResponseDto);
    }

    @PreAuthorize("hasRole('ADMIN') or @taskRepository.findById(#taskId).orElse(null)?.user?.id == authentication.principal.id")
    public TaskResponseDto update(final Long taskId, final CreateTaskDto taskDto) {
        log.info("Mise à jour de la tâche ID: {}", taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
        User user = userRepository.findById(taskDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException(taskDto.getUserId()));

        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setDone(taskDto.isDone());
        task.setUser(user);

        Task taskUpdated = taskRepository.save(task);
        log.info("Tâche ID: {} mise à jour avec succès", taskId);
        return taskMapper.toResponseDto(taskUpdated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void delete(final Long id) {
        log.info("Suppression de la tâche ID: {}", id);
        if (!taskRepository.existsById(id)) {
            log.warn("Suppression impossible: tâche ID: {} non trouvée", id);
            throw new TaskNotFoundException(id);
        }
        taskRepository.deleteById(id);
        log.info("Tâche ID: {} supprimée", id);
    }
}