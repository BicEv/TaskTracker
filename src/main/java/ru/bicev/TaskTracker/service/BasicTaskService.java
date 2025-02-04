package ru.bicev.TaskTracker.service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import ru.bicev.TaskTracker.dto.TaskDto;
import ru.bicev.TaskTracker.entity.Task;
import ru.bicev.TaskTracker.entity.User;
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

    @Transactional
    @Override
    public TaskDto createTask(TaskDto taskDto) {
        Task task = taskRepository.save(TaskMapper.fromDto(taskDto, userRepository));
        return TaskMapper.fromEntity(task);
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

    @Override
    public TaskDto getTaskById(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task with id " + taskId + " is not found"));
        return TaskMapper.fromEntity(task);
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
        task.setCreatedAt(taskDto.getCreatedAt());
        task.setStatus(TaskStatus.fromString(taskDto.getStatus()));
        task.setCompletedAt(taskDto.getCompletedAt());
        Task updatedTask = taskRepository.save(task);
        return TaskMapper.fromEntity(updatedTask);
    }

    public List<TaskDto> getTasksForCurrentUser(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return taskRepository.findTasksByUserId(user.getId())
                .stream()
                .map(TaskMapper::fromEntity)
                .collect(Collectors.toList());

    }

    private boolean validateUserAccess(User currentUser, Task task) {
        return currentUser.getId().equals(task.getUser().getId()) || currentUser.getRole() == Role.ADMIN;
    }

}
