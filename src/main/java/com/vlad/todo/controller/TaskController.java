package com.vlad.todo.controller;

import com.vlad.todo.dto.TaskDtoRequest;
import com.vlad.todo.dto.TaskDtoResponse;
import com.vlad.todo.exception.InvalidInputException;
import com.vlad.todo.service.TaskService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Задачи",
        description = """
                API для управления задачами.
                • Обычный пользователь может видеть и менять ТОЛЬКО свои задачи.
                • Админ может работать с задачами любых пользователей.
                """
)
@RestController
@CrossOrigin(origins = "http://localhost:8081")
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // ===============================
    //      GET ALL TASKS
    // ===============================
    @Operation(
            summary = "Получить список задач",
            description = """
                    • USER — получает только свои задачи  
                    • ADMIN — получает все задачи  
                    Можно фильтровать по completed=true|false
                    """
    )
    @GetMapping
    public ResponseEntity<List<TaskDtoResponse>> getTasks(
            @Parameter(description = "Статус завершения (true/false). Необязательно.")
            @RequestParam(required = false) Boolean completed
    ) {
        List<TaskDtoResponse> tasks = taskService.findAllTasks();

        if (completed != null) {
            tasks = tasks.stream()
                    .filter(t -> t.getIsCompleted() != null && t.getIsCompleted().equals(completed))
                    .toList();
        }

        return ResponseEntity.ok(tasks);
    }

    // ===============================
    //      GET USER TASKS
    // ===============================
    @Operation(
            summary = "Получить задачи пользователя",
            description = """
                    • USER — может указывать только СВОЙ userId  
                    • ADMIN — может указывать любой userId
                    """
    )
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<TaskDtoResponse>> getTasksByUser(
            @Parameter(description = "ID пользователя") @PathVariable long userId
    ) {
        if (userId < 1) throw new InvalidInputException("Id должен быть больше 0");

        return ResponseEntity.ok(taskService.findTasksByUser(userId));
    }

    // ===============================
    //        CREATE TASK
    // ===============================
    @Operation(
            summary = "Создать задачу",
            description = """
                    • USER — создаёт задачу ТОЛЬКО себе (переданный userId игнорируется).  
                    • ADMIN — может создать задачу любому пользователю (userId обязателен).
                    """
    )
    @PostMapping
    public ResponseEntity<TaskDtoResponse> createTask(
            @Parameter(description = "Данные новой задачи")
            @Valid @RequestBody TaskDtoRequest taskDto
    ) {
        TaskDtoResponse saved = taskService.saveTask(taskDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // ===============================
    //        UPDATE TASK
    // ===============================
    @Operation(
            summary = "Обновить задачу",
            description = """
                    • USER — может обновлять только свои задачи  
                    • ADMIN — любые задачи
                    """
    )
    @PutMapping("/{id}")
    public ResponseEntity<TaskDtoResponse> updateTask(
            @Parameter(description = "ID задачи") @PathVariable long id,
            @Parameter(description = "Обновлённые данные задачи") @RequestBody TaskDtoRequest taskDto
    ) {
        if (id < 1) throw new InvalidInputException("Id должен быть больше 0");

        return ResponseEntity.ok(taskService.updateTask(id, taskDto));
    }

    // ===============================
    //          GET BY ID
    // ===============================
    @Operation(
            summary = "Получить задачу по ID",
            description = """
                    • USER — может получать только свои задачи  
                    • ADMIN — любые задачи
                    """
    )
    @GetMapping("/{id}")
    public ResponseEntity<TaskDtoResponse> getTaskById(
            @Parameter(description = "ID задачи") @PathVariable long id
    ) {
        if (id < 1) throw new InvalidInputException("Id должен быть больше 0");

        return ResponseEntity.ok(taskService.findTaskById(id));
    }

    // ===============================
    //        DELETE TASK
    // ===============================
    @Operation(
            summary = "Удалить задачу",
            description = """
                    • USER — может удалять только свои задачи  
                    • ADMIN — любые задачи
                    """
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "ID задачи") @PathVariable long id
    ) {
        if (id < 1) throw new InvalidInputException("Id должен быть больше 0");

        taskService.deleteTaskById(id);
        return ResponseEntity.ok().build();
    }
}
