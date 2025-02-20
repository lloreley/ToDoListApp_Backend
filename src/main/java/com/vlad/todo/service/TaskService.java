package com.vlad.todo.service;

import com.vlad.todo.model.Task;
import java.util.List;

public interface TaskService {
    List<Task> findAllTasks();

    Task findTaskById(int id);

    Task saveTask(Task task);

    Task updateTask(Task task);

    void deleteTaskById(int id);
}
