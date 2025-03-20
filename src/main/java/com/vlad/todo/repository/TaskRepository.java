package com.vlad.todo.repository;

import com.vlad.todo.model.TaskEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    Optional<TaskEntity> findById(long id);

    void deleteById(long id);
}
