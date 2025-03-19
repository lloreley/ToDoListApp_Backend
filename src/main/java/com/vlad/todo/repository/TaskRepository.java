package com.vlad.todo.repository;

import com.vlad.todo.model.Task;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Optional<Task> findById(long id);

    Boolean existsByTitle(String title);

    void deleteById(long id);
}
