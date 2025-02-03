package ru.bicev.TaskTracker.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.bicev.TaskTracker.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
