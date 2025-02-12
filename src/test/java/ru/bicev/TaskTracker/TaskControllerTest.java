package ru.bicev.TaskTracker;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import ru.bicev.TaskTracker.entity.Task;
import ru.bicev.TaskTracker.entity.User;
import ru.bicev.TaskTracker.repo.TaskRepository;
import ru.bicev.TaskTracker.repo.UserRepository;
import ru.bicev.TaskTracker.util.Role;
import ru.bicev.TaskTracker.util.TaskStatus;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeAll
    void setUp() {
        User testUser1 = new User(null, "testUser", "password", Role.USER);
        testUser1.setPassword(passwordEncoder.encode("password"));
        User testUser2 = new User(null, "testUser2", "password", Role.USER);
        testUser2.setPassword(passwordEncoder.encode("password"));
        User testAdmin = new User(null, "testAdmin", "password", Role.ADMIN);
        testAdmin.setPassword(passwordEncoder.encode("password"));
        userRepository.saveAll(List.of(testUser1, testUser2, testAdmin));

        Task testTask1 = new Task(null, testUser1, "Test title", "Test description", LocalDateTime.now(), null,
                TaskStatus.NEW);
        Task testTask2 = new Task(null, testUser1, "Test title 2", "Test description 2", LocalDateTime.now(), null,
                TaskStatus.PENDING);
        Task testTask3 = new Task(null, testUser1, "Test title 3", "Test description 3", LocalDateTime.now(), null,
                TaskStatus.NEW);
        Task testTask4 = new Task(null, testUser2, "Test title 4", "Test description 4", LocalDateTime.now(), null,
                TaskStatus.PENDING);
        Task testTask5 = new Task(null, testUser2, "Test title 5", "Test description 5", LocalDateTime.now(), null,
                TaskStatus.NEW);
        taskRepository.saveAll(List.of(testTask1, testTask2, testTask3, testTask4, testTask5));
    }

    private String createTaskJson(String taskTitle, String taskDescription, String status) {
        return """
                {
                    "title": "%s",
                    "description": "%s",
                    "status": "%s"
                }
                """.formatted(taskTitle, taskDescription, status);
    }

    @Test
    @DisplayName("Task creation for authorized user")
    void createTaskTest() throws Exception {

        String taskJson = createTaskJson("New task", "Description", "NEW");
        mockMvc.perform(post("/api/tasks")
                .with(httpBasic("testUser", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskJson))
                .andExpect(status().isCreated());

    }

    @Test
    @DisplayName("Get task by Id")
    void getTaskByIdTest() throws Exception {
        mockMvc.perform(get("/api/tasks/1")
                .with(httpBasic("testUser", "password")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get task by Id 403")
    void getTaskById403Test() throws Exception {
        mockMvc.perform(get("/api/tasks/1")
                .with(httpBasic("testUser2", "password")))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Get tasks for current user")
    void getTasksForCurrentUser() throws Exception {
        mockMvc.perform(get("/api/tasks")
                .with(httpBasic("testUser", "password")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Update task")
    void updateTaskTest() throws Exception {
        String updateJson = createTaskJson("Updated title", "Updated description", "COMPLETED");

        mockMvc.perform(put("/api/tasks/1")
                .with(httpBasic("testUser", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Update task 403")
    void updateTask403Test() throws Exception {
        String updateJson = createTaskJson("Updated title", "Updated description", "COMPLETED");

        mockMvc.perform(put("/api/tasks/1")
                .with(httpBasic("testUser2", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Update task 404")
    void updateTask404Test() throws Exception {
        String updateJson = createTaskJson("Updated title", "Updated description", "COMPLETED");

        mockMvc.perform(put("/api/tasks/15")
                .with(httpBasic("testUser", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete task")
    void deleteTaskTest() throws Exception {
        mockMvc.perform(delete("/api/tasks/1")
                .with(httpBasic("testUser", "password")))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Delete task 403")
    void deleteTask403Test() throws Exception {
        mockMvc.perform(delete("/api/tasks/2")
                .with(httpBasic("testUser2", "password")))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Delete task 404")
    void deleteTask404Test() throws Exception {
        mockMvc.perform(delete("/api/tasks/15")
                .with(httpBasic("testUser", "password")))
                .andExpect(status().isNotFound());
    }

}
