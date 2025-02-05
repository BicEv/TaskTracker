package ru.bicev.TaskTracker.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.bicev.TaskTracker.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

}
