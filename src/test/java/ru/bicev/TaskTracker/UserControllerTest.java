package ru.bicev.TaskTracker;

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

import jakarta.transaction.Transactional;
import ru.bicev.TaskTracker.entity.User;
import ru.bicev.TaskTracker.repo.UserRepository;
import ru.bicev.TaskTracker.util.Role;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeAll
    void setUp() {

        User testUser = new User(null, "testUser", "password", Role.USER);
        testUser.setPassword(passwordEncoder.encode("password"));
        User testUser2 = new User(null, "testUser2", "password", Role.USER);
        testUser2.setPassword(passwordEncoder.encode("password"));
        User testAdmin = new User(null, "admin", "password", Role.ADMIN);
        testAdmin.setPassword(passwordEncoder.encode("password"));
        userRepository.saveAll(List.of(testUser, testUser2, testAdmin));

    }

    @Test
    @DisplayName("User creation")
    void createUserTest() throws Exception {
        String userJson = """
                {
                    "username": "newUser",
                    "password": "password",
                    "role": "USER"
                }
                """;
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated());

    }

    @Test
    @DisplayName("User creation conflict")
    void createUserConflictTest() throws Exception {
        String userJson = """
                {
                    "username": "testUser",
                    "password": "password",
                    "role": "USER"
                }
                """;
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Get user by Id success")
    void getUserByIdTest() throws Exception {
        mockMvc.perform(get("/api/users/2")
                .with(httpBasic("admin", "password")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get user by Id 404")
    void getUserById404Test() throws Exception {
        mockMvc.perform(get("/api/users/10")
                .with(httpBasic("admin", "password")))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get user by username")
    void getUserByUsernameTest() throws Exception {
        mockMvc.perform(get("/api/users/username")
                .param("username", "testUser")
                .with(httpBasic("admin", "password")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get user by username 404")
    void getUserByUsername404Test() throws Exception {
        mockMvc.perform(get("/api/users/username")
                .param("username", "notAUser")
                .with(httpBasic("admin", "password")))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Update user")
    void updateUserTest() throws Exception {
        String userJson = """
                {
                    "username": "updatedUser",
                    "password": "password",
                    "role": "ADMIN"
                }
                """;

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson)
                .with(httpBasic("testUser", "password")))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("Update user 403")
    void updateUser403Test() throws Exception {
        String userJson = """
                {
                    "username": "updatedUser",
                    "password": "password",
                    "role": "ADMIN"
                }
                """;

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson)
                .with(httpBasic("testUser2", "password")))
                .andExpect(status().isForbidden());

    }

    @Test
    @DisplayName("Update user 404")
    void updateUser404Test() throws Exception {
        String userJson = """
                {
                    "username": "updatedUser",
                    "password": "password",
                    "role": "ADMIN"
                }
                """;

        mockMvc.perform(put("/api/users/15")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson)
                .with(httpBasic("testUser", "password")))
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Delete user")
    void deleteUserTest() throws Exception {
        mockMvc.perform(delete("/api/users/1")
                .with(httpBasic("testUser", "password")))
                .andExpect(status().isNoContent());

    }

    @Test
    @DisplayName("Delete user 404")
    void deleteUser404Test() throws Exception{
        mockMvc.perform(delete("/api/users/10")
                .with(httpBasic("admin", "password")))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete user 403")
    void deleteUser403Test() throws Exception{
        mockMvc.perform(delete("/api/users/3")
                .with(httpBasic("testUser2", "password")))
                .andExpect(status().isForbidden());
    }

}
