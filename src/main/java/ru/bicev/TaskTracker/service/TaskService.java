package ru.bicev.TaskTracker.service;

import java.security.Principal;

import ru.bicev.TaskTracker.dto.TaskDto;

public interface TaskService {

    TaskDto createTask(TaskDto taskDto);

    TaskDto getTaskById(Long taskId);

    TaskDto updateTask(Long taskId, TaskDto taskDto, Principal principal);

    void deleteTask(Long taskId, Principal principal);
}
