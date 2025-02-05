package ru.bicev.TaskTracker.service;

import java.security.Principal;
import java.util.List;

import ru.bicev.TaskTracker.dto.TaskDto;

public interface TaskService {

    TaskDto createTask(TaskDto taskDto, Principal principal);

    TaskDto getTaskById(Long taskId, Principal principal);

    TaskDto updateTask(Long taskId, TaskDto taskDto, Principal principal);

    void deleteTask(Long taskId, Principal principal);

    List<TaskDto> getTasksForCurrentUser(Principal principal);
}
