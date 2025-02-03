package ru.bicev.TaskTracker.util;

import ru.bicev.TaskTracker.dto.UserDto;
import ru.bicev.TaskTracker.entity.User;

public class UserMapper {

    public static User fromDto(UserDto userDto) {
        User user = new User();
        if (userDto.getId() != null) {
            user.setId(userDto.getId());
        }
        user.setUsername(userDto.getUsername());
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            user.setPassword(userDto.getPassword());
        }
        user.setRole(Role.fromString(userDto.getRole()));
        return user;
    }

    public static UserDto fromEntity(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setRole(user.getRole().toString());

        return userDto;
    }

}
