package ru.bicev.TaskTracker.exceptions;

public class TaskNotFoundException extends NotFoundException {
    public TaskNotFoundException(String message) {
        super(message);
    }

}
