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
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import ru.bicev.TaskTracker.dto.UserDto;
import ru.bicev.TaskTracker.entity.User;
import ru.bicev.TaskTracker.exceptions.AccessDeniedException;
import ru.bicev.TaskTracker.exceptions.DuplicateUserException;
import ru.bicev.TaskTracker.exceptions.UserNotFoundException;
import ru.bicev.TaskTracker.repo.UserRepository;
import ru.bicev.TaskTracker.service.BasicUserService;
import ru.bicev.TaskTracker.util.Role;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private BasicUserService userService;

    private User testUser = new User(1L, "testUser", "encodedPassword", Role.USER);
    private User notAUser = new User(2L, "notAUser", "notAPassword", Role.USER);
    private User updatedUser = new User(1L, "updatedUser", "updatedEncodedPassword", Role.ADMIN);
    private UserDto testUserDto = new UserDto(1L, "testUser", "rawPassword", "USER");
    private UserDto updatedUserDto = new UserDto(1L, "updatedUser", "updatedPassword", "ADMIN");

    @Test
    void testCreateUserSuccess() {

        when(userRepository.existsByUsername(testUserDto.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(testUserDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDto result = userService.createUser(testUserDto);

        assertNotNull(result);
        assertEquals(testUserDto.getUsername(), result.getUsername());
        assertEquals(testUserDto.getRole(), result.getRole());
        verify(passwordEncoder, times(1)).encode("rawPassword");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUserDuplicate() {

        when(userRepository.existsByUsername(testUserDto.getUsername())).thenReturn(true);

        assertThrows(DuplicateUserException.class, () -> userService.createUser(testUserDto));
    }

    @Test
    void testDeleteUserSuccess() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testUser");

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));

        userService.deleteUser(1L, principal);

        verify(userRepository, times(1)).deleteById(1L);
        verify(userRepository, times(1)).findByUsername("testUser");
    }

    @Test
    void testDeleteUserNotFound() {
        Principal principal = mock(Principal.class);

        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L, principal));
    }

    @Test
    void testDeleteUserAccessDenied() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("notAUser");

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.findByUsername("notAUser")).thenReturn(Optional.of(notAUser));

        assertThrows(AccessDeniedException.class, () -> userService.deleteUser(1L, principal));

    }

    @Test
    void testGetUserByIdSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserDto result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(testUserDto.getUsername(), result.getUsername());
        assertEquals(testUserDto.getRole(), result.getRole());
    }

    @Test
    void testGetUserByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void testGetUserByUsernameSuccess() {
        when(userRepository.findByUsername(testUserDto.getUsername())).thenReturn(Optional.of(testUser));

        UserDto result = userService.getUserByUsername(testUserDto.getUsername());

        assertNotNull(result);
        assertEquals(testUserDto.getUsername(), result.getUsername());
        assertEquals(testUserDto.getRole(), result.getRole());

    }

    @Test
    void testGetUserByUsernameNotFound() {
        when(userRepository.findByUsername(testUserDto.getUsername())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserByUsername(testUserDto.getUsername()));
    }

    @Test
    void testUpdateUserSuccess() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testUser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("updatedPassword")).thenReturn("updatedEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto result = userService.updateUser(1L, updatedUserDto, principal);

        assertNotNull(result);
        assertEquals("updatedUser", result.getUsername());
        assertEquals("ADMIN", result.getRole());
        verify(passwordEncoder, times(1)).encode("updatedPassword");
        verify(userRepository, times(1)).save(any(User.class));

    }

    @Test
    void testUpdateUserNotFound() {
        Principal principal = mock(Principal.class);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(1L, updatedUserDto, principal));
    }

    @Test
    void testUpdateUserAccessDenied() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("notAUser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername("notAUser")).thenReturn(Optional.of(notAUser));

        assertThrows(AccessDeniedException.class, () -> userService.updateUser(1L, updatedUserDto, principal));

    }

}
