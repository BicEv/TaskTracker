package ru.bicev.TaskTracker.util;

import org.springframework.beans.factory.annotation.Autowired;

import ru.bicev.TaskTracker.dto.TaskDto;
import ru.bicev.TaskTracker.entity.Task;
import ru.bicev.TaskTracker.entity.User;
import ru.bicev.TaskTracker.repo.UserRepository;
import ru.bicev.TaskTracker.exceptions.UserNotFoundException;

public class TaskMapper {

    public static Task fromDto(TaskDto taskDto, UserRepository userRepo) {
        Task task = new Task();

        if (taskDto.getId() != null) {
            task.setId(taskDto.getId());
        }
        task.setTitle(taskDto.getTitle());

        if (taskDto.getUserId() != null) {
            User user = userRepo.findById(taskDto.getUserId())
                .orElseThrow(() - new UserNotFoundException("User with id " + taskDto.getUserId() + " not found"));
            task.setUser(user);
        }
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
        taskDto.setUserId(task.getUser().getId());
        taskDto.setDescription(task.getDescription());
        taskDto.setCreatedAt(task.getCreatedAt());
        taskDto.setCompletedAt(task.getCompletedAt());
        taskDto.setStatus(task.getStatus().toString());

        return taskDto;
    }

}
