package ru.bicev.TaskTracker.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.bicev.TaskTracker.entity.Task;
import java.util.List;
import ru.bicev.TaskTracker.util.TaskStatus;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUserId(Long userId);

    List<Task> findByStatus(TaskStatus taskStatus);

    List<Task> findByUserIdAndStatus(Long userId, TaskStatus status);
}
