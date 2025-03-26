package com.vlad.todo.controller;

import com.vlad.todo.dto.TaskDtoRequest;
import com.vlad.todo.dto.TaskDtoResponse;
import com.vlad.todo.exception.CreationException;
import com.vlad.todo.exception.NotFoundException;
import com.vlad.todo.exception.UpdateException;
import com.vlad.todo.service.TaskService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskDtoResponse> tasksByFilter(
            @RequestParam(required = false) Boolean completed) {
        if (completed != null) {
            return taskService.findAllTasks().stream()
                    .filter(taskDtoResponse -> taskDtoResponse.getIsCompleted() != null
                            && taskDtoResponse.getIsCompleted() == completed)
                    .toList();
        }
        return taskService.findAllTasks();
    }

    @GetMapping("/by-user/{userId}")
    public List<TaskDtoResponse> tasksByUser(@PathVariable long userId) {
        return taskService.findTasksByUser(userId);
    }

    @PostMapping("/saveTask")
    public TaskDtoResponse saveTask(@RequestBody TaskDtoRequest taskDto) {
        return taskService.saveTask(taskDto);
    }

    @PutMapping("/{id}")
    public TaskDtoResponse updateTask(@PathVariable long id, @RequestBody TaskDtoRequest taskDto) {
        return taskService.updateTask(id, taskDto);
    }

    @GetMapping("/{id}")
    public TaskDtoResponse findTaskById(@PathVariable long id) {
        return taskService.findTaskById(id);
    }

    @DeleteMapping("/deleteTask/{id}")
    public void deleteTaskById(@PathVariable long id) {
        taskService.deleteTaskById(id);
    }

    @ExceptionHandler(CreationException.class)
    public ResponseEntity<String> handleCreateException(CreationException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(UpdateException.class)
    public ResponseEntity<String> handleUpdateException(UpdateException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
