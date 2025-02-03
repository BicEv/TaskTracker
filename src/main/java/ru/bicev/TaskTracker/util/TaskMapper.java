package ru.bicev.TaskTracker.util;

import ru.bicev.TaskTracker.dto.TaskDto;
import ru.bicev.TaskTracker.entity.Task;

public class TaskMapper {

    public static Task fromDto(TaskDto taskDto) {
        Task task = new Task();

        if (taskDto.getId() != null) {
            task.setId(taskDto.getId());
        }
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setCreatedAt(taskDto.getCreatedAt());
        task.setCompletedAt(taskDto.getCompletedAt());
        task.setStatus(TaskStatus.fromString(taskDto.getStatus()));

        return task;
    }

    public static TaskDto fromEntity(Task task) {
        TaskDto taskDto = new TaskDto();
        taskDto.setId(task.getId());
        taskDto.setTitle(task.getTitle());
        taskDto.setDescription(task.getDescription());
        taskDto.setCreatedAt(task.getCreatedAt());
        taskDto.setCompletedAt(task.getCompletedAt());
        taskDto.setStatus(task.getStatus().toString());

        return taskDto;
    }

}
