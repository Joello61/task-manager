package com.example.task_manager.repository;

import com.example.task_manager.entity.Task;
import com.example.task_manager.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findAllByUser(User user, Pageable pageable);
    Optional<Task> findByTitle(String title);
}
