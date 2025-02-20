package com.vlad.todo.controller;


import com.vlad.todo.model.Task;
import com.vlad.todo.service.TaskService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @GetMapping
    public List<Task> tasksByFilter(@RequestParam(required = false) Boolean completed) {
        List<Task> filteredTasks = taskService.findAllTasks();
        if (completed != null) {
            filteredTasks = taskService.findAllTasks().stream()
                            .filter(todo -> todo.getCompleted() == completed)
                            .collect(Collectors.toList());
        }
        return filteredTasks;
    }


    @PostMapping("saveTask")
    public Task saveTask(@RequestBody Task task) {
        return taskService.saveTask(task);
    }

    @GetMapping("/{id}")
    public Task findTaskById(@PathVariable int id) {
        return taskService.findTaskById(id);
    }

    @DeleteMapping("deleteTask/{id}")
    public void deleteTaskById(@PathVariable int id) {
        taskService.deleteTaskById(id);
    }

    @PutMapping("updateTask")
    public Task updateTaskById(Task task) {
        return taskService.updateTask(task);
    }

}
