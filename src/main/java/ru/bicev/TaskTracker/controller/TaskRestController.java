package ru.bicev.TaskTracker.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import ru.bicev.TaskTracker.dto.TaskDto;
import ru.bicev.TaskTracker.service.TaskService;

@RestController
@RequestMapping("/api/tasks")
public class TaskRestController {

    private final TaskService taskService;

    @Autowired
    public TaskRestController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody TaskDto taskDto, Principal principal) {
        TaskDto createdTask = taskService.createTask(taskDto, principal);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long taskId, Principal principal) {
        TaskDto task = taskService.getTaskById(taskId, principal);
        return ResponseEntity.ok().body(task);
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasksForCurrentUser(Principal principal) {
        List<TaskDto> tasks = taskService.getTasksForCurrentUser(principal);
        return ResponseEntity.ok().body(tasks);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long taskId, @Valid @RequestBody TaskDto taskDto,
            Principal principal) {
        TaskDto updatedTask = taskService.updateTask(taskId, taskDto, principal);
        return ResponseEntity.ok().body(updatedTask);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId, Principal principal) {
        taskService.deleteTask(taskId, principal);
        return ResponseEntity.noContent().build();
    }

}
