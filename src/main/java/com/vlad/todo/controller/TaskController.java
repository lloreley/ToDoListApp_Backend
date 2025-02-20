package com.vlad.todo.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.vlad.todo.model.Task;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping  ("/api/v1/tasks")
public class TaskController {

    public List<Task> findAllTasks(){
        return List.of(
          new Task("Eat", LocalDate.now())
        );
    }
}
