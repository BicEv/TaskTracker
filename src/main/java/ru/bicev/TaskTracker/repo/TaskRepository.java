package ru.bicev.TaskTracker.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.bicev.TaskTracker.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

}
