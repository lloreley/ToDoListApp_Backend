package com.vlad.todo.repository;

import com.vlad.todo.model.Task;
import com.vlad.todo.service.TaskService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;


@Repository
public class TaskDaoImpl implements TaskService {
    private final List<Task> tasks = new ArrayList<>();

    @Override
    public List<Task> findAllTasks() {
        return tasks;
    }

    @Override
    public Task findTaskById(int id) {
        return tasks.stream().filter(task -> task.getId() == id).findFirst().orElse(null);
    }

    @Override
    public Task saveTask(Task task) {
        tasks.add(task);
        return task;
    }

    @Override
    public Task updateTask(Task task) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == task.getId()) {
                tasks.set(i, task);
                return task;
            }
        }
        return null;
        // throw new RuntimeException("Task not found");
    }

    @Override
    public void deleteTaskById(int id) {
        Task task = findTaskById(id);
        if (task != null) {
            tasks.remove(task);
        }
        // throw new RuntimeException("Task not found");
    }
}

