package ru.bicev.TaskTracker;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.Principal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.bicev.TaskTracker.controller.GlobalExceptionHandler;
import ru.bicev.TaskTracker.controller.TaskRestController;
import ru.bicev.TaskTracker.controller.UserRestCotroller;
import ru.bicev.TaskTracker.dto.TaskDto;
import ru.bicev.TaskTracker.dto.UserDto;
import ru.bicev.TaskTracker.exceptions.AccessDeniedException;
import ru.bicev.TaskTracker.exceptions.DuplicateUserException;
import ru.bicev.TaskTracker.exceptions.TaskNotFoundException;
import ru.bicev.TaskTracker.exceptions.UserNotFoundException;
import ru.bicev.TaskTracker.service.BasicTaskService;
import ru.bicev.TaskTracker.service.BasicUserService;

@WebMvcTest({ TaskRestController.class, UserRestCotroller.class, GlobalExceptionHandler.class })
@AutoConfigureMockMvc(addFilters = false)
public class GlobalExceptionHandlerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BasicUserService userService;

    @MockitoBean
    private BasicTaskService taskService;

    private TaskDto testTask;
    private UserDto testUser;

    @BeforeEach
    void setUp() {
        testTask = new TaskDto(1L, 1L, "NAN", "NAN", LocalDateTime.now(), null, "NEW");
        testUser = new UserDto(1L, "testUser", "01111110", "USER");
    }

    @Test
    void testCreateTask404() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testUser");
        when(taskService.createTask(any(TaskDto.class), any(Principal.class)))
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTask))
                .principal(principal))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetTaskById404() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testUser");
        when(taskService.getTaskById(1L, principal)).thenThrow(new TaskNotFoundException("Task not found"));

        mockMvc.perform(get("/api/tasks/1")
                .principal(principal))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetTaskById403() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testUser");
        when(taskService.getTaskById(1L, principal)).thenThrow(new AccessDeniedException("Access denied"));

        mockMvc.perform(get("/api/tasks/1")
                .principal(principal))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateUser409() throws Exception {
        when(userService.createUser(testUser)).thenThrow(new DuplicateUserException("Duplicate user"));

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isConflict());
    }

    @Test
    void testUpdateUser500() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testUser");
        when(userService.updateUser(1L, testUser, principal)).thenThrow(new RuntimeException("Runtime exception"));

        mockMvc.perform(put("/api/users/1")
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isInternalServerError());
    }

}
