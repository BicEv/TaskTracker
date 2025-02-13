package ru.bicev.TaskTracker.service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import ru.bicev.TaskTracker.dto.TaskDto;
import ru.bicev.TaskTracker.entity.Task;
import ru.bicev.TaskTracker.entity.User;
import ru.bicev.TaskTracker.exceptions.AccessDeniedException;
import ru.bicev.TaskTracker.exceptions.TaskNotFoundException;
import ru.bicev.TaskTracker.exceptions.UserNotFoundException;
import ru.bicev.TaskTracker.repo.TaskRepository;
import ru.bicev.TaskTracker.repo.UserRepository;
import ru.bicev.TaskTracker.util.Role;
import ru.bicev.TaskTracker.util.TaskMapper;
import ru.bicev.TaskTracker.util.TaskStatus;

@Service
public class BasicTaskService implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Autowired
    public BasicTaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    //To-Do: remove unnecessary UserNotFound bc Security configuration accepts only authorized Users

    @Transactional
    @Override
    public TaskDto createTask(TaskDto taskDto, Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Task task = TaskMapper.fromDto(taskDto);
        task.setUser(currentUser);
        Task createdTask = taskRepository.save(task);
        return TaskMapper.fromEntity(createdTask);
    }

    @Override
    public TaskDto getTaskById(Long taskId, Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task with id " + taskId + " is not found"));
        if (!validateUserAccess(currentUser, task)) {
            throw new AccessDeniedException("You are not allowed to get this task");
        }
        return TaskMapper.fromEntity(task);
    }

    @Override
    public List<TaskDto> getTasksForCurrentUser(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return taskRepository.findTasksByUserId(user.getId())
                .stream()
                .map(TaskMapper::fromEntity)
                .collect(Collectors.toList());

    }

    @Transactional
    @Override
    public TaskDto updateTask(Long taskId, TaskDto taskDto, Principal principal) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task with id " + taskId + " is not found"));
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (!validateUserAccess(user, task)) {
            throw new AccessDeniedException("You are not allowed to edit this task");
        }

        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        if (taskDto.getCreatedAt() != null) {
            task.setCreatedAt(taskDto.getCreatedAt());
        }
        task.setStatus(TaskStatus.fromString(taskDto.getStatus()));
        if (taskDto.getStatus().equalsIgnoreCase("COMPLETED")) {
            task.setCompletedAt(LocalDateTime.now());
        } else if (taskDto.getCompletedAt() != null) {
            task.setCompletedAt(taskDto.getCompletedAt());
        }

        Task updatedTask = taskRepository.save(task);
        return TaskMapper.fromEntity(updatedTask);
    }

    @Transactional
    @Override
    public void deleteTask(Long taskId, Principal principal) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task with id " + taskId + " is not found"));
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (!validateUserAccess(user, task)) {
            throw new AccessDeniedException("You are not allowed to delete this task");
        }
        taskRepository.deleteById(taskId);

    }

    private boolean validateUserAccess(User currentUser, Task task) {
        return currentUser.getId().equals(task.getUser().getId()) || currentUser.getRole() == Role.ADMIN;
    }

}
