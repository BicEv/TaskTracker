package ru.bicev.TaskTracker;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.bicev.TaskTracker.controller.TaskRestController;
import ru.bicev.TaskTracker.dto.TaskDto;
import ru.bicev.TaskTracker.service.BasicTaskService;

@WebMvcTest(TaskRestController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TaskRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BasicTaskService taskService;

    private List<TaskDto> tasks;
    private TaskDto testTask;
    private TaskDto testTask1;
    private TaskDto testTask2;
    private TaskDto updatedTask;

    @BeforeEach
    void setUp() {
        testTask = new TaskDto(1L, 1L, "Test title", "Test description", LocalDateTime.now(), null, "NEW");
        testTask1 = new TaskDto(2L, 1L, "Test title2", "Test description2", LocalDateTime.now(), null, "PENDING");
        testTask2 = new TaskDto(3L, 1L, "Test title3", "Test description3", LocalDateTime.now(), null, "COMPLETED");
        updatedTask = new TaskDto(1L, 1L, "Updated title", "Updated description",
                LocalDateTime.of(2020, 6, 2, 0, 0, 0, 0), null, "PENDING");
        tasks = List.of(testTask, testTask1, testTask2);
    }

    @Test
    void testCreateTaskSuccess() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testUser");
        when(taskService.createTask(any(TaskDto.class), any(Principal.class))).thenReturn(testTask);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTask))
                .principal(principal))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testTask.getId()))
                .andExpect(jsonPath("$.description").value(testTask.getDescription()));

        verify(taskService, times(1)).createTask(any(TaskDto.class), any(Principal.class));

    }

    @Test
    void testGetTaskByIdSuccess() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testUser");
        when(taskService.getTaskById(1L, principal)).thenReturn(testTask);

        mockMvc.perform(get("/api/tasks/1")
                .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testTask.getId()))
                .andExpect(jsonPath("$.description").value(testTask.getDescription()));

        verify(taskService, times(1)).getTaskById(1L, principal);
    }

    @Test
    void testGetAllTasksForCurrentUserSuccess() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testUser");
        when(taskService.getTasksForCurrentUser(principal)).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks")
                .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(tasks.size()))
                .andExpect(jsonPath("$[0].title").value(testTask.getTitle()))
                .andExpect(jsonPath("$[0].description").value(testTask.getDescription()))
                .andExpect(jsonPath("$[1].title").value(testTask1.getTitle()))
                .andExpect(jsonPath("$[1].description").value(testTask1.getDescription()))
                .andExpect(jsonPath("$[2].title").value(testTask2.getTitle()))
                .andExpect(jsonPath("$[2].description").value(testTask2.getDescription()));

        verify(taskService, times(1)).getTasksForCurrentUser(principal);
    }

    @Test
    void testUpdateTaskSuccess() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testUser");
        when(taskService.updateTask(1L, updatedTask, principal)).thenReturn(updatedTask);

        mockMvc.perform(put("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTask))
                .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(updatedTask.getTitle()))
                .andExpect(jsonPath("$.description").value(updatedTask.getDescription()));

        verify(taskService, times(1)).updateTask(1L, updatedTask, principal);
    }

    @Test
    void testDeleteTaskSuccess() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testUser");
        doNothing().when(taskService).deleteTask(1L, principal);

        mockMvc.perform(delete("/api/tasks/1")
                .principal(principal))
                .andExpect(status().isNoContent());

        verify(taskService, times(1)).deleteTask(1L, principal);
    }

}
