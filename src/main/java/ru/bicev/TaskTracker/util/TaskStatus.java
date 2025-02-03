package ru.bicev.TaskTracker.util;

public enum TaskStatus {

    NEW, PENDING, COMPLETED;

    public static TaskStatus fromString(String taskStatus) {
        if (taskStatus.equalsIgnoreCase("new"))
            return NEW;
        else if (taskStatus.equalsIgnoreCase("pending"))
            return PENDING;
        else if (taskStatus.equalsIgnoreCase("completed"))
            return COMPLETED;
        else
            throw new IllegalArgumentException("Invalid task status");

    }

    public String toString() {
        return this.name();
    }

}
