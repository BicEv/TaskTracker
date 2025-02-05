package ru.bicev.TaskTracker.exceptions;

public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException(String message) {
        super(message);
    }

}
