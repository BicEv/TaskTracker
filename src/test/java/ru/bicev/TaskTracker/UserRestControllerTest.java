package ru.bicev.TaskTracker;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.security.Principal;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.bicev.TaskTracker.controller.UserRestCotroller;
import ru.bicev.TaskTracker.dto.UserDto;
import ru.bicev.TaskTracker.service.BasicUserService;

@WebMvcTest(UserRestCotroller.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BasicUserService userService;

    private UserDto testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserDto(1L, "testUser", "password", "USER");
    }

    @Test
    void testCreateUserSuccess() throws Exception {
        when(userService.createUser(any(UserDto.class))).thenReturn(testUser);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testUser.getId()))
                .andExpect(jsonPath("$.username").value(testUser.getUsername()));

        verify(userService).createUser(any(UserDto.class));

    }

    @Test
    void getUserById_AdminAccess_ShouldReturnUser() throws Exception {
        when(userService.getUserById(1L)).thenReturn(testUser);

        mockMvc.perform(get("/api/users/1")
                .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId()))
                .andExpect(jsonPath("$.username").value(testUser.getUsername()));

        verify(userService).getUserById(1L);
    }

    @Test
    void getUserByUsername_AdminAccess_ShouldReturnUser() throws Exception {
        when(userService.getUserByUsername("testUser")).thenReturn(testUser);

        mockMvc.perform(get("/api/users/username").param("username", "testUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(testUser.getUsername()));

        verify(userService).getUserByUsername("testUser");
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("testUser");
        when(userService.updateUser(eq(1L), any(UserDto.class), any(Principal.class))).thenReturn(testUser);

        mockMvc.perform(put("/api/users/1")
                .principal(mockPrincipal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(testUser.getUsername()));

        verify(userService).updateUser(eq(1L), any(UserDto.class), any(Principal.class));
    }

    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("testUser");
        doNothing().when(userService).deleteUser(eq(1L), any(Principal.class));

        mockMvc.perform(delete("/api/users/1").principal(mockPrincipal))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(eq(1L), any(Principal.class));
    }
}
