package ru.bicev.TaskTracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TaskDto {

    private Long id;

    @NotNull(message = "UserId is required")
    private Long userId;

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must be less than 101 symbols long")
    private String title;

    @Size(max = 1000, message = "Description must be less than 1001 symbols long")
    private String description;

    @NotBlank(message = "Status is required")
    private String status;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z$", message = "StartTime must be in ISO 8601 format (e.g., 2024-12-17T15:30:00Z)")
    private String startTime;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z$", message = "EndTime must be in ISO 8601 format (e.g., 2024-12-17T15:30:00Z)")
    private String endTime;

    private Long duration;

}
