package com.vlad.todo.service;

import com.vlad.todo.dto.TaskDtoRequest;
import com.vlad.todo.dto.TaskDtoResponse;
import com.vlad.todo.dto.UserDtoResponse;
import com.vlad.todo.exception.NotFoundException;
import com.vlad.todo.exception.UpdateException;
import com.vlad.todo.mapper.TaskMapper;
import com.vlad.todo.model.Task;
import com.vlad.todo.model.User;
import com.vlad.todo.repository.TaskRepository;
import com.vlad.todo.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Transactional
public class TaskService {
    private final TaskMapper taskMapper;
    private TaskRepository taskRepository;
    private UserRepository userRepository;

    public List<TaskDtoResponse> findAllTasks() {
        List<TaskDtoResponse> tasksDtoResponse = new ArrayList<>();
        taskRepository.findAll().forEach(
                task -> tasksDtoResponse.add(taskMapper.toDto(task)));
        return tasksDtoResponse;
    }

    public List<TaskDtoResponse> findTasksByUser(long userId) {
        List<Task> tasks = taskRepository.findByUser(userId);
        List<TaskDtoResponse> tasksDtoResponse = new ArrayList<>();

        tasks.forEach(task -> tasksDtoResponse.add(taskMapper.toDto(task)));
        return tasksDtoResponse;
    }

    public TaskDtoResponse findTaskById(long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Task with id %d not found", id)));
        return taskMapper.toDto(task);
    }

    public TaskDtoResponse saveTask(TaskDtoRequest taskDtoRequest) {
        User user = userRepository.findById(taskDtoRequest.getUserId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("User with id %d not found", taskDtoRequest.getUserId())));
        Task task = taskMapper.toEntity(taskDtoRequest);
        task.setUser(user);
        taskRepository.save(task);
        return taskMapper.toDto(task);
    }

    public TaskDtoResponse updateTask(long id, TaskDtoRequest taskDtoRequest) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Task with id %d not found", id)));

        if (taskDtoRequest.getTitle() != null) {
            task.setTitle(taskDtoRequest.getTitle());
        }
        if (taskDtoRequest.getContent() != null) {
            task.setContent(taskDtoRequest.getContent());
        }
        if (taskDtoRequest.getIsCompleted() != null) {
            task.setIsCompleted(taskDtoRequest.getIsCompleted());
        }
        if (taskDtoRequest.getDeadlineDate() != null) {
            task.setDeadlineDate(taskDtoRequest.getDeadlineDate());
        }
        if (taskDtoRequest.getIsImportant() != null) {
            task.setIsImportant(taskDtoRequest.getIsImportant());
        }
        try {
            taskRepository.save(task);
            return taskMapper.toDto(task);
        } catch (DataIntegrityViolationException ex) {
            throw new UpdateException("Error updating task with id: " + id);
        }
    }

    public void deleteTaskById(long id) {
        if (!taskRepository.existsById(id)) {
            throw new NotFoundException(
                String.format("Task with id %d not found.", id));
        }
        taskRepository.deleteById(id);
    }
}
