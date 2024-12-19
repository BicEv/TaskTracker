package ru.bicev.TaskTracker.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.bicev.TaskTracker.dto.TaskDto;
import ru.bicev.TaskTracker.entity.Task;
import ru.bicev.TaskTracker.exceptions.TaskNotFoundException;
import ru.bicev.TaskTracker.repo.TaskRepository;
import ru.bicev.TaskTracker.util.TaskMapper;
import ru.bicev.TaskTracker.util.TaskStatus;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    public Task createTask(TaskDto taskDto) {
        Task task = TaskMapper.toEntity(taskDto);
        if (task.getStartTime() == null) {
            task.setStartTime(Instant.now());
        }
        return taskRepository.save(task);
    }

    public Task getTask(Long taskId) {
        return getTaskById(taskId);
    }

    @Transactional
    public Task updateTask(Long taskId, TaskDto taskDto) {
        Task task = getTaskById(taskId);
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        if (task.getStatus() == TaskStatus.COMPLETED) {
            throw new IllegalArgumentException("Task is already completed");
        }
        task.setStatus(TaskStatus.fromString(taskDto.getStatus()));
        if (taskDto.getStartTime() != null && !taskDto.getStartTime().isEmpty()) {
            task.setStartTime(Instant.parse(taskDto.getStartTime()));
        }
        if (taskDto.getEndTime() != null && !taskDto.getEndTime().isEmpty()) {
            task.setEndTime(Instant.parse(taskDto.getEndTime()));
        }
        if (task.getStatus() == TaskStatus.COMPLETED) {
            completeTask(task);
        }
        return taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        Task task = getTaskById(taskId);
        taskRepository.delete(task);
    }

    @Transactional
    public void updateTaskStatus(Long taskId, TaskStatus taskStatus) {
        Task task = getTaskById(taskId);
        task.setStatus(taskStatus);
        if (taskStatus == TaskStatus.COMPLETED) {
            completeTask(task);
        }
        taskRepository.save(task);
    }

    public List<Task> getTasksByUserId(Long userId) {
        return taskRepository.findByUserId(userId);
    }

    public List<Task> getTasksByUserIdAndStatus(Long userId, TaskStatus taskStatus) {
        return taskRepository.findByUserIdAndStatus(userId, taskStatus);
    }

    public List<Task> getTasksByStatus(TaskStatus taskStatus) {
        return taskRepository.findByStatus(taskStatus);
    }

    private Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task with id:" + taskId + " is not found"));
    }

    private void completeTask(Task task) {
        task.setEndTime(Instant.now());
        task.setDuration(Duration.between(task.getStartTime(), task.getEndTime()).toMillis());
    }

}
