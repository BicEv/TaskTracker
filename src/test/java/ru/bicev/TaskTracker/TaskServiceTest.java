package ru.bicev.TaskTracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.bicev.TaskTracker.dto.TaskDto;
import ru.bicev.TaskTracker.entity.Task;
import ru.bicev.TaskTracker.entity.User;
import ru.bicev.TaskTracker.exceptions.AccessDeniedException;
import ru.bicev.TaskTracker.exceptions.TaskNotFoundException;
import ru.bicev.TaskTracker.exceptions.UserNotFoundException;
import ru.bicev.TaskTracker.repo.TaskRepository;
import ru.bicev.TaskTracker.repo.UserRepository;
import ru.bicev.TaskTracker.service.BasicTaskService;
import ru.bicev.TaskTracker.util.Role;
import ru.bicev.TaskTracker.util.TaskStatus;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BasicTaskService taskService;

    User testUser = new User(1L, "testUser", "encodedPassword", Role.USER);
    User notAUser = new User(2L, "notAUser", "notAPassword", Role.USER);
    User testAdmin = new User(10L, "testAdmin", "encodedAdminPassword", Role.ADMIN);

    Task testTask = new Task(1L, testUser, "Test title", "Test description", LocalDateTime.now(), null, TaskStatus.NEW);
    Task testTask1 = new Task(1L, testUser, "Test title1", "Test description1", LocalDateTime.now(), null,
            TaskStatus.PENDING);
    Task testTask2 = new Task(1L, testUser, "Test title2", "Test description2", LocalDateTime.now(), null,
            TaskStatus.COMPLETED);
    List<Task> tasks = List.of(testTask, testTask1, testTask2);
    Task updatedTask = new Task(1L, testUser, "Updated title", "Updated description", LocalDateTime.now(), null,
            TaskStatus.PENDING);
    TaskDto testTaskDto = new TaskDto(1L, 1L, "Test title", "Test description", LocalDateTime.now(), null, "NEW");
    TaskDto updatedTaskDto = new TaskDto(1L, 1L, "Updated title", "Updated description", LocalDateTime.now(), null,
            "PENDING");

    @Test
    void testCreateTaskSuccess() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testUser");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        TaskDto result = taskService.createTask(testTaskDto, principal);

        assertNotNull(result);
        assertEquals(testTaskDto.getTitle(), result.getTitle());
        assertEquals(testTaskDto.getDescription(), result.getDescription());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testCreateTaskNotFound() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("notFound");

        when(userRepository.findByUsername("notFound")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> taskService.createTask(testTaskDto, principal));
    }

    @Test
    void testGetTaskByIdSuccess() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testUser");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        TaskDto result = taskService.getTaskById(1L, principal);

        assertNotNull(result);
        assertEquals(testTask.getTitle(), result.getTitle());
        assertEquals(testTask.getDescription(), result.getDescription());
        assertEquals(testTask.getStatus().toString(), result.getStatus());

    }

    @Test
    void testGetTaskByIdNotFound() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(taskRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(2L, principal));

        Principal notFound = mock(Principal.class);
        when(notFound.getName()).thenReturn("notFound");
        when(userRepository.findByUsername("notFound")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> taskService.getTaskById(1L, notFound));

    }

    @Test
    void testGetTaskByIdAccessDenied() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("notAUser");
        when(userRepository.findByUsername("notAUser")).thenReturn(Optional.of(notAUser));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        assertThrows(AccessDeniedException.class, () -> taskService.getTaskById(1L, principal));

    }

    @Test
    void testGetTasksForCurrentUserSuccess() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(taskRepository.findTasksByUserId(1L)).thenReturn(tasks);

        List<TaskDto> taskDtos = taskService.getTasksForCurrentUser(principal);

        assertNotNull(taskDtos);
        assertEquals(tasks.size(), taskDtos.size());

        assertEquals(testTask.getTitle(), taskDtos.get(0).getTitle());
        assertEquals(testTask.getDescription(), taskDtos.get(0).getDescription());
        assertEquals(testTask.getStatus().toString(), taskDtos.get(0).getStatus());

        assertEquals(testTask1.getTitle(), taskDtos.get(1).getTitle());
        assertEquals(testTask1.getDescription(), taskDtos.get(1).getDescription());
        assertEquals(testTask1.getStatus().toString(), taskDtos.get(1).getStatus());

        assertEquals(testTask2.getTitle(), taskDtos.get(2).getTitle());
        assertEquals(testTask2.getDescription(), taskDtos.get(2).getDescription());
        assertEquals(testTask2.getStatus().toString(), taskDtos.get(2).getStatus());

    }

    @Test
    void testGetTasksForCurrentUserNotFound() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("notFound");
        when(userRepository.findByUsername("notFound")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> taskService.getTasksForCurrentUser(principal));
    }

    @Test
    void testUpdateTaskSuccess() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        TaskDto result = taskService.updateTask(1L, updatedTaskDto, principal);

        assertNotNull(result);
        assertEquals(updatedTask.getTitle(), result.getTitle());
        assertEquals(updatedTask.getDescription(), result.getDescription());
        assertEquals(updatedTask.getStatus().toString(), result.getStatus());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testUpdateTaskNotFound() {
        Principal principal = mock(Principal.class);
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(1L, updatedTaskDto, principal));
    }

    @Test
    void testUpdateTaskUserNotFound() {
        Principal notFound = mock(Principal.class);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(notFound.getName()).thenReturn("notFound");
        when(userRepository.findByUsername("notFound")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> taskService.updateTask(1L, updatedTaskDto, notFound));
    }

    @Test
    void testUpdateTaskAccessDenied() {
        Principal principal = mock(Principal.class);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(principal.getName()).thenReturn("notAUser");
        when(userRepository.findByUsername("notAUser")).thenReturn(Optional.of(notAUser));

        assertThrows(AccessDeniedException.class, () -> taskService.updateTask(1L, updatedTaskDto, principal));
    }

    @Test
    void testDeleteTaskSuccess() {
        Principal principal = mock(Principal.class);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(principal.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        taskService.deleteTask(1L, principal);

        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteTaskAdminSuccess() {
        Principal principal = mock(Principal.class);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(principal.getName()).thenReturn("testAdmin");
        when(userRepository.findByUsername("testAdmin")).thenReturn(Optional.of(testAdmin));
        taskService.deleteTask(1L, principal);

        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteTaskNotFound() {
        Principal principal = mock(Principal.class);
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(1L, principal));
    }

    @Test
    void testDeleteTaskUserNotFound() {
        Principal principal = mock(Principal.class);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        assertThrows(UserNotFoundException.class, () -> taskService.deleteTask(1L, principal));
    }

    @Test
    void testDeleteTaskAccessDenied() {
        Principal principal = mock(Principal.class);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(principal.getName()).thenReturn("notAUser");
        when(userRepository.findByUsername("notAUser")).thenReturn(Optional.of(notAUser));

        assertThrows(AccessDeniedException.class, () -> taskService.deleteTask(1L, principal));
    }
}
