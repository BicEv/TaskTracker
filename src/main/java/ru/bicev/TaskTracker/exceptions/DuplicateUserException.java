package ru.bicev.TaskTracker.exceptions;

public class DuplicateUserException extends RuntimeException {

    public DuplicateUserException(String messge) {
        super(messge);
    }

}
