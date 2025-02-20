package com.vlad.todo.controller;


import com.vlad.todo.model.Task;
//import java.time.LocalDate;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping  ("/api/v1/tasks")
public class TaskController {
    public List<Task> findAllTasks() {
        return List.of(
                new Task("Sleep")
        );
    }

}
