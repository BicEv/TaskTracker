package ru.bicev.TaskTracker.util;

public enum Role {
    USER, ADMIN;

    public static Role fromString(String role) {
        if (role.equalsIgnoreCase("admin")) {
            return ADMIN;
        } else
            return USER;
    }

    public String toString() {
        return this.name();
    }

}
