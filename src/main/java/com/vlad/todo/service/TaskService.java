package com.vlad.todo.service;


import com.vlad.todo.dto.TaskDto;
import com.vlad.todo.exception.CreationException;
import com.vlad.todo.exception.NotFoundException;
import com.vlad.todo.exception.UpdateException;
import com.vlad.todo.mapper.TaskMapper;
import com.vlad.todo.model.Task;
import com.vlad.todo.repository.TaskRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@AllArgsConstructor
public class TaskService {
    private final TaskMapper taskMapper;
    private TaskRepository taskRepository;

    public List<Task> findAllTasks() {
        return taskRepository.findAll();
    }

    public ResponseEntity<Task> findTaskById(long id) {
        return taskRepository.findById(id).map(
                value -> new ResponseEntity<>(value, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    public Task saveTask(@RequestBody TaskDto taskDto) {
        if (taskRepository.existsByTitle(taskDto.getTitle())) {
            throw new CreationException("Task with the same title already exists");
        }
        Task task = taskMapper.fromTaskDto(taskDto);
        return taskRepository.save(task);
    }

    public Task updateTask(long id, @RequestBody TaskDto taskDto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Task with id %d not found", id))
                );

        if (taskDto.getTitle() != null) {
            task.setTitle(taskDto.getTitle());
        }
        if (taskDto.getContent() != null) {
            task.setContent(taskDto.getContent());
        }
        if (taskDto.getIsCompleted() != null) {
            task.setIsCompleted(taskDto.getIsCompleted());
        }
        if (taskDto.getTaskDeadline() != null) {
            task.setTaskDeadline(taskDto.getTaskDeadline());
        }
        if (taskDto.getIsImportant() != null) {
            task.setIsImportant(taskDto.getIsImportant());
        }
        try {
            return taskRepository.save(task);
        } catch (DataIntegrityViolationException ex) {
            throw new UpdateException(
                    "Error updating task with title: " + taskDto.getTitle()
            );
        }
    }

    public void deleteTaskById(long id) {
        if (!taskRepository.existsById(id)) {
            System.out.printf("Task with id %d not found.", id);
            throw new NotFoundException(
                String.format("Task with id %d not found.", id)
            );
        }
        taskRepository.deleteById(id);
    }
}
