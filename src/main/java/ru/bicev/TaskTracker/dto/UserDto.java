package ru.bicev.TaskTracker.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDto {

    private Long id;

    @NotEmpty
    private String username;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    private String role;

}
