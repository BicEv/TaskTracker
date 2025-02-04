package ru.bicev.TaskTracker.service;

import ru.bicev.TaskTracker.dto.TaskDto;

public interface TaskService {

    TaskDto createTask(TaskDto taskDto);

    TaskDto getTaskById(Long taskId);

    TaskDto updateTask(Long taskId, TaskDto taskDto);

    void deleteTask(Long taskId);
}
