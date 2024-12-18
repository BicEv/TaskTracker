package ru.bicev.TaskTracker.util;

import java.time.Instant;

import ru.bicev.TaskTracker.dto.TaskDto;
import ru.bicev.TaskTracker.entity.Task;

public class TaskMapper {

    public static TaskDto toDto(Task task) {
        TaskDto taskDto = new TaskDto();
        taskDto.setId(task.getId());
        taskDto.setUserId(task.getUserId());
        taskDto.setTitle(task.getTitle());
        taskDto.setDescription(task.getDescription());
        taskDto.setStatus(task.getStatus().toString());
        taskDto.setStartTime(task.getStartTime() != null ? task.getStartTime().toString() : null);
        taskDto.setEndTime(task.getEndTime() != null ? task.getEndTime().toString() : null);
        taskDto.setDuration(task.getDuration());

        return taskDto;
    }

    public static Task toEntity(TaskDto taskDto) {
        Task task = new Task();
        if (taskDto.getId() != null)
            task.setId(taskDto.getId());
        task.setUserId(taskDto.getUserId());
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setStartTime(taskDto.getStartTime() != null ? Instant.parse(taskDto.getStartTime()) : null);
        task.setEndTime(taskDto.getEndTime() != null ? Instant.parse(taskDto.getEndTime()) : null);
        task.setStatus(taskDto.getStatus() != null ? TaskStatus.fromString(taskDto.getStatus()) : null);

        return task;
    }

}
