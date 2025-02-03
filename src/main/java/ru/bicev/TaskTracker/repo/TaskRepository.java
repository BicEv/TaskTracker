package ru.bicev.TaskTracker.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.bicev.TaskTracker.entity.Task;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE t.user.id = :userId")
    List<Task> findTasksByUserId(@Param("userId") Long userId);

    //Method to check Data JPA possibilities  
    List<Task> findByUser_Id(Long userId);

}
