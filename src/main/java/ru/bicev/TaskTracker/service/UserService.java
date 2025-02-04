package ru.bicev.TaskTracker.service;

import ru.bicev.TaskTracker.dto.UserDto;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto getUserById(Long userId);

    UserDto updateUser(Long userId, UserDto userDto);

    void deleteUser(Long userId);

    UserDto getUserByUsername(String username);

}