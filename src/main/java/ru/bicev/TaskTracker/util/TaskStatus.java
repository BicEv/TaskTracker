package ru.bicev.TaskTracker.util;

import java.util.Arrays;

public enum TaskStatus {
    OPEN, IN_PROGRESS, COMPLETED;

    public static TaskStatus fromString(String status){
        return Arrays.stream(TaskStatus.values())
        .filter(s->s.name().equalsIgnoreCase(status))
        .findFirst()
        .orElseThrow(()-> new IllegalArgumentException("Invalid TaskStatus: " + status));
        
    }

}
