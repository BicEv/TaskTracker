package ru.bicev.TaskTracker.service;

import java.security.Principal;

import ru.bicev.TaskTracker.dto.UserDto;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto getUserById(Long userId);

    UserDto updateUser(Long userId, UserDto userDto, Principal principal);

    void deleteUser(Long userId, Principal principal);

    UserDto getUserByUsername(String username);

}