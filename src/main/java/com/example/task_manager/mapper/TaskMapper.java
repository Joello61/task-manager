package com.example.task_manager.mapper;

import com.example.task_manager.dto.task.CreateTaskDto;
import com.example.task_manager.dto.task.TaskResponseDto;
import com.example.task_manager.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface TaskMapper {
    TaskResponseDto toResponseDto(Task task);

    @Mapping(target = "user", ignore = true)
    Task toEntity(CreateTaskDto createTaskDto);
}
